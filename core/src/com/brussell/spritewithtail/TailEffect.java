package com.brussell.spritewithtail;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TailEffect {
  private final ParticleEffect _dragTrail;
  private final Sprite _spriteToFollow;

  public TailEffect(final Sprite spriteToFollow) {
    _dragTrail = new ParticleEffect();
    _dragTrail.load(Gdx.files.internal("trail.p"), Gdx.files.internal(""));
    _spriteToFollow = spriteToFollow;
  }

  public void render(final SpriteBatch batch, final float deltaTime) {
    _dragTrail.setPosition(_spriteToFollow.getX() + _spriteToFollow.getOriginX(), _spriteToFollow.getY() + _spriteToFollow.getOriginY());
    _dragTrail.draw(batch, deltaTime);
  }

  public void start() {
    _dragTrail.start();
  }

  public void end() {
    _dragTrail.allowCompletion();
  }
}