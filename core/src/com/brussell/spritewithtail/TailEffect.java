package com.brussell.spritewithtail;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface TailEffect {
  void render(final SpriteBatch batch, final float deltaTime);

  void start();

  void end();

  void setRender(final boolean render);
}