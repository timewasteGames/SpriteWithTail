package com.brussell.spritewithtail;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SpriteWithTail extends ApplicationAdapter {
  private static final int NUM_CONTROL_POINTS = 10;
  private static final float SPEED = 20f;

  // Rendering
  private SpriteBatch _spriteBatch;
  private Sprite _sprite;
  private final Array<TailEffect> _tailEffects = new Array<TailEffect>();
  private int _activeTailEffect;

  // The flight path definition
  private Vector2[] _controlPoints;
  private CatmullRomSpline<Vector2> _catmullRomSpline;
  private Vector2 _oldHeading = new Vector2();
  private Vector2 _newHeading = new Vector2();

  // Traversing the flight path
  private float _progressionAcc;
  private Vector2 _tempVec2 = new Vector2();

  public void create() {
    _spriteBatch = new SpriteBatch();

    // The Nyan cat
    _sprite = new Sprite(new Texture(Gdx.files.internal("nyanCat.png")));
    _sprite.setOriginCenter();

    // The spline
    _controlPoints = new Vector2[NUM_CONTROL_POINTS];
    initCatmullRomSpline();

    // Tail effect
    _tailEffects.add(new TailEffectRainbow(_sprite));
    _tailEffects.add(new TailEffectMultiColored(_sprite));
    _tailEffects.add(new TailEffectHangAbout(_sprite));
    _activeTailEffect = 0;

    Gdx.input.setInputProcessor(new GestureDetector(new GameListener()));
  }

  private class GameListener extends GestureDetector.GestureAdapter {
    @Override
    public boolean tap(final float x, final float y, final int count, final int button) {
      initCatmullRomSpline();
      for (TailEffect tailEffect : _tailEffects) {
        tailEffect.start();
      }
      return false;
    }

    @Override
    public boolean fling(final float velocityX, final float velocityY, final int button) {
      // Cycle the tail effect currently displayed.
      _tailEffects.get(_activeTailEffect).setRender(false);
      _activeTailEffect = _activeTailEffect == _tailEffects.size - 1 ? 0 : _activeTailEffect + 1;
      _tailEffects.get(_activeTailEffect).setRender(true);
      return false;
    }
  }

  private void initCatmullRomSpline() {
    Array<Vector2> controlPoints = new Array<Vector2>();
    // Takeoff
    controlPoints.add(getFlightPathVector(0.5f, 0f));
    controlPoints.add(getFlightPathVector(0.8f, 0.2f));

    // Aerobatics
    for (int i = 2; i < NUM_CONTROL_POINTS - 1; i++) {
      int infiniteLoopProtection = 0;
      Vector2 newControlPoint = new Vector2();
      float deviation;
      do {
        setFlightPathVector(MathUtils.random(), MathUtils.random(), newControlPoint);
        infiniteLoopProtection++;

        _oldHeading.set(controlPoints.get(i - 1)).sub(controlPoints.get(i - 2));
        _newHeading.set(newControlPoint).sub(controlPoints.get(i - 1));
        deviation = Math.abs(_oldHeading.angle(_newHeading));
      }
      while (deviation > 90f && infiniteLoopProtection < 500);
      controlPoints.add(newControlPoint);
    }
    // Landing
    controlPoints.add(getFlightPathVector(0.2f, 0.2f));

    // Create the spline and reset the accumulator
    _controlPoints = controlPoints.toArray(Vector2.class);
    _catmullRomSpline = new CatmullRomSpline<Vector2>(_controlPoints, true);
    _progressionAcc = 0f;
  }

  private Vector2 getFlightPathVector(final float fractionScreenX, final float fractionScreenY) {
    return setFlightPathVector(fractionScreenX, fractionScreenY, new Vector2());
  }

  private Vector2 setFlightPathVector(final float fractionScreenX, final float fractionScreenY, final Vector2 flightPathVector) {
    flightPathVector.set(Gdx.graphics.getWidth() * fractionScreenX, Gdx.graphics.getHeight() * fractionScreenY);
    return flightPathVector;
  }

  public void render() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    _spriteBatch.begin();
    for (TailEffect tailEffect : _tailEffects) {
      tailEffect.render(_spriteBatch, Gdx.graphics.getDeltaTime());
    }
    _sprite.draw(_spriteBatch);
    _spriteBatch.end();

    update(Gdx.graphics.getDeltaTime());
  }

  private void update(final float deltaTime) {
    _catmullRomSpline.derivativeAt(_tempVec2, _progressionAcc);
    _progressionAcc += (deltaTime * SPEED) / _tempVec2.len();

    // We are still flying if the accumulator has not reached 1.
    if (_progressionAcc < 1f) {
      _catmullRomSpline.derivativeAt(_tempVec2, _progressionAcc);
      _sprite.setRotation(_tempVec2.angle());

      _catmullRomSpline.valueAt(_tempVec2, _progressionAcc);
      _sprite.setPosition(_tempVec2.x, _tempVec2.y);
    }
    // End up nicely where we started.
    else {
      for (TailEffect tailEffect : _tailEffects) {
        tailEffect.end();
      }
      _sprite.setPosition(_controlPoints[0].x, _controlPoints[0].y);
      _sprite.setRotation(0);
    }
  }

  public void resize(int width, int height) {
    // Just recreate the batch to apply the new screen settings.
    _spriteBatch.dispose();
    _spriteBatch = new SpriteBatch();
  }
}