package com.max.myfirstmpdemo.GameAssetsAndStuff;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.max.myfirstmpdemo.MyFirstMpDemoMain;

public class AsteroidBall {
    MyFirstMpDemoMain game;
    public static Animation<TextureRegion> asteroidAnimation;
    public Sprite keyframe;



    public Vector2 position = new Vector2(); //null position is (0,0) by default
    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    public AsteroidBall(MyFirstMpDemoMain game) {
        this.game = game;
        asteroidAnimation = new Animation<TextureRegion>(1/5f, game.splashScreen.gameAssets.asteroidTextureAtlas.findRegions("asteroid"));
        asteroidAnimation.setPlayMode(Animation.PlayMode.LOOP);
        keyframe = new Sprite(game.splashScreen.gameAssets.asteroidTextureAtlas.createSprites().get(0));
    }

    public float stateTime = 0;

    public void update(float delta){
        // -->this to be done later along with rotation asteroidAnimation.setFrameDuration();
        keyframe.setRegion(asteroidAnimation.getKeyFrame(stateTime));
        keyframe.setPosition(position.x, position.y);
        if(true){ //true will be false when state on server side is static
        stateTime += delta;}
        keyframe.draw(game.getBatch());
    }

}