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

public class SpriteWithTail extends ApplicationAdapter {
  private static final int NUM_CONTROL_POINTS = 10;
  private static final float SPEED = 10f;

  // Rendering
  private SpriteBatch _spriteBatch;
  private Sprite _sprite;
  private TailEffect _tailEffect;

  // The flight path definition
  private Vector2[] _controlPoints;
  private CatmullRomSpline<Vector2> _catmullRomSpline;

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
    _tailEffect = new TailEffectHangAbout(_sprite);
    _tailEffect.start();

    Gdx.input.setInputProcessor(new GestureDetector(new GameListener()));
  }

  private class GameListener extends GestureDetector.GestureAdapter {
    @Override
    public boolean tap(final float x, final float y, final int count, final int button) {
      initCatmullRomSpline();
      _tailEffect.start();
      return false;
    }
  }

  private void initCatmullRomSpline() {
    Vector2 startEndPos = new Vector2(Gdx.graphics.getWidth() * 0.5f, 0);
    Vector2 takeoffPos = new Vector2(Gdx.graphics.getWidth() * 0.8f, Gdx.graphics.getHeight() * 0.2f);
    Vector2 approachPos = new Vector2(Gdx.graphics.getWidth() * 0.2f, Gdx.graphics.getHeight() * 0.2f);

    // Takeoff
    _controlPoints[0] = startEndPos;
    _controlPoints[1] = takeoffPos;
    // Aerobatics
    for (int i = 2; i < NUM_CONTROL_POINTS - 1; i++) {
      Vector2 pointOnFlightPath = new Vector2();
      int avoidInfiniteLoopCount = 0;
      do {
        pointOnFlightPath.set(MathUtils.random(Gdx.graphics.getWidth()), MathUtils.random(Gdx.graphics.getHeight()));
        avoidInfiniteLoopCount++;
      }
      // Prefer to not flip back on ourselves, but OK if can't avoid in reasonable number of tries
      while (Math.abs(pointOnFlightPath.angle(_controlPoints[i - 1])) > 90f && avoidInfiniteLoopCount < 100);
      _controlPoints[i] = pointOnFlightPath;
    }
    // Landing
    _controlPoints[NUM_CONTROL_POINTS - 1] = approachPos;

    // Create the spline and reset the accumulator
    _catmullRomSpline = new CatmullRomSpline<Vector2>(_controlPoints, true);
    _progressionAcc = 0f;
  }

  public void render() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    _spriteBatch.begin();
    _tailEffect.render(_spriteBatch, Gdx.graphics.getDeltaTime());
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
      _tailEffect.end();
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