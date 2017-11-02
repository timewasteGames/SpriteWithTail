package com.brussell.spritewithtail;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TailEffectRainbow implements TailEffect {
  private static final float ARC_DEGREES = 100f;

  private final Sprite _spriteToFollow;

  private final Array<ParticleEffect> _dragTrails = new Array<ParticleEffect>();
  private final Array<ParticleEmitter.RangedNumericValue> _xOffsets = new Array<ParticleEmitter.RangedNumericValue>();
  private final Array<ParticleEmitter.RangedNumericValue> _yOffsets = new Array<ParticleEmitter.RangedNumericValue>();

  public TailEffectRainbow(final Sprite spriteToFollow) {
    // Create multiple effects with a different color each.
    addNewTrailEffect(Color.RED);
    addNewTrailEffect(Color.ORANGE);
    addNewTrailEffect(Color.YELLOW);
    addNewTrailEffect(Color.GREEN);
    addNewTrailEffect(Color.BLUE);
    addNewTrailEffect(new Color(0.29f, 0f, 0.51f, 1f)); // Indigo
    addNewTrailEffect(Color.VIOLET);
    _spriteToFollow = spriteToFollow;
  }

  private void addNewTrailEffect(final Color color) {
    ParticleEffect particleEffect = new ParticleEffect();
    particleEffect.load(Gdx.files.internal("smallTrail.p"), Gdx.files.internal(""));

    // Set the colour
    final ParticleEmitter.GradientColorValue tint = particleEffect.getEmitters().first().getTint();
    tint.setColors(new float[]{color.r, color.g, color.b});
    tint.setTimeline(new float[]{0f});

    final ParticleEmitter.RangedNumericValue offsetValueX = particleEffect.getEmitters().first().getXOffsetValue();
    offsetValueX.setLow(0f);
    offsetValueX.setActive(true);
    final ParticleEmitter.RangedNumericValue offsetValueY = particleEffect.getEmitters().first().getYOffsetValue();
    offsetValueY.setLow(0f);
    offsetValueY.setActive(true);

    _dragTrails.add(particleEffect);
    _xOffsets.add(offsetValueX);
    _yOffsets.add(offsetValueY);
  }

  @Override
  public void render(final SpriteBatch batch, final float deltaTime) {
    Vector2 offsetVec = new Vector2(Vector2.X);
    // Put emitters in a arc behind the sprite.
    final float rotationIncrements = ARC_DEGREES / _dragTrails.size;
    offsetVec.rotate(_spriteToFollow.getRotation() + 175f - ARC_DEGREES / 2f).scl(_spriteToFollow.getWidth() * 0.3f);
    for (int i = 0; i < _dragTrails.size; i++) {
      _dragTrails.get(i).setPosition(_spriteToFollow.getX() + _spriteToFollow.getOriginX(), _spriteToFollow.getY() + _spriteToFollow.getOriginY());
      offsetVec.rotate(rotationIncrements);
      _xOffsets.get(i).setLow(offsetVec.x);
      _yOffsets.get(i).setLow(offsetVec.y);
      _dragTrails.get(i).draw(batch, deltaTime);
    }
  }

  @Override
  public void start() {
    for (ParticleEffect particleEffect : _dragTrails) {
      particleEffect.start();
    }
  }

  @Override
  public void end() {
    for (ParticleEffect particleEffect : _dragTrails) {
      particleEffect.allowCompletion();
    }
  }
}
