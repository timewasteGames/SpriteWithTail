package com.brussell.spritewithtail;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TailEffectHangAbout implements TailEffect {
  private final ParticleEffect _dragTrail;
  private final Sprite _spriteToFollow;

  public TailEffectHangAbout(final Sprite spriteToFollow) {
    _dragTrail = new ParticleEffect();
    _dragTrail.load(Gdx.files.internal("hangAboutTrail.p"), Gdx.files.internal(""));
    _spriteToFollow = spriteToFollow;
  }

  @Override
  public void render(final SpriteBatch batch, final float deltaTime) {
    _dragTrail.setPosition(_spriteToFollow.getX() + _spriteToFollow.getOriginX(), _spriteToFollow.getY() + _spriteToFollow.getOriginY());
    _dragTrail.draw(batch, deltaTime);
    if (_dragTrail.isComplete()) {
      _dragTrail.start();
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
}
