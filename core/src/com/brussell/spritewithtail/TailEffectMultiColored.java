package com.brussell.spritewithtail;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TailEffectMultiColored implements TailEffect {
  private final ParticleEffect _dragTrail;
  private final Sprite _spriteToFollow;
  private boolean _render = false;
  private final ParticleEmitter.ScaledNumericValue _particleRotation;

  public TailEffectMultiColored(final Sprite spriteToFollow) {
    _dragTrail = new ParticleEffect();
    _dragTrail.load(Gdx.files.internal("trail.p"), Gdx.files.internal(""));
    _particleRotation = _dragTrail.getEmitters().first().getRotation();
    _particleRotation.setActive(true);
    _spriteToFollow = spriteToFollow;
  }

  @Override
  public void render(final SpriteBatch batch, final float deltaTime) {
    _dragTrail.setPosition(_spriteToFollow.getX() + _spriteToFollow.getOriginX(), _spriteToFollow.getY() + _spriteToFollow.getOriginY());
    _particleRotation.setHigh(_spriteToFollow.getRotation());
    if (_render) {
      _dragTrail.draw(batch, deltaTime);
    }
    else {
      _dragTrail.update(deltaTime);
    }
  }

  @Override
  public void start() {
    _dragTrail.start();
  }

  @Override
  public void end() {
    _dragTrail.allowCompletion();
  }

  @Override
  public void setRender(final boolean render) {
    _render = render;
  }
}
