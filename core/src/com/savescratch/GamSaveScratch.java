package com.savescratch;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;

//Sources:		Preferences: https://github.com/libgdx/libgdx/wiki/Preferences
//							http://stackoverflow.com/questions/18607689/how-to-save-game-state-preferences-in-android-using-libgdx

public class GamSaveScratch extends ApplicationAdapter {
	private static final int    nCols = 4;
	private static final int    nRows = 4;

	Preferences prefCoords;
	SpriteBatch sbMain;
	Texture txSprite;
	Texture txBackground;
	TextureRegion[] trFrames;
	TextureRegion trCurrentFrame;
	float fSpriteX;
	float fSpriteY=0;
	float fSpriteSpeed=45f;
	float fTime=0f;
	Animation aniMain;

	TiledMap tmGameMap1;
	OrthogonalTiledMapRenderer orthotmrRenderer;
	OrthographicCamera ocMainCam;

	@Override
	public void create () {
		sbMain = new SpriteBatch();
		txBackground= new Texture(Gdx.files.internal("lostwoods2.jpg"));
		txSprite = new Texture(Gdx.files.internal("CinderellaSpriteSheet.png"));
		prefCoords = Gdx.app.getPreferences("Coordinates");
		fSpriteX = prefCoords.getFloat("Last X-Coord");
		fSpriteY = prefCoords.getFloat("Last Y-Coord");
		System.out.println("Starting SpriteX" + prefCoords.getFloat("Last X-Coord"));
		TextureRegion[][] tmp = TextureRegion.split(txSprite,txSprite.getWidth()/nCols,txSprite.getHeight()/nRows);
		trFrames = new TextureRegion[nCols * nRows];
		int index = 0;
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				trFrames[index++] = tmp[i][j];
			}
		}
		ocMainCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		ocMainCam.update();
		aniMain= new Animation(1f,trFrames);
		tmGameMap1= new TmxMapLoader().load("GameMap1.tmx");
		orthotmrRenderer = new OrthogonalTiledMapRenderer(tmGameMap1);
	}

	@Override
	public void render () {
		if(fTime<4){
			fTime += Gdx.graphics.getDeltaTime();}
		else{
			fTime=0;
		}

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		trCurrentFrame = aniMain.getKeyFrame(0);

		if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) {
			fSpriteX -= Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			prefCoords.putFloat("Last X-Coord", fSpriteX);
			trCurrentFrame = aniMain.getKeyFrame(4+fTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
			fSpriteX += Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			prefCoords.putFloat("Last X-Coord", fSpriteX);
			trCurrentFrame = aniMain.getKeyFrame(8+fTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)) {
			fSpriteY += Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			prefCoords.putFloat("Last Y-Coord", fSpriteY);
			trCurrentFrame = aniMain.getKeyFrame(12+fTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)) {
			fSpriteY -= Gdx.graphics.getDeltaTime() * fSpriteSpeed;
			prefCoords.putFloat("Last Y-Coord", fSpriteY);
			trCurrentFrame = aniMain.getKeyFrame(0+fTime);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)){
			prefCoords.putFloat("Last X-Coord", 0);
			prefCoords.putFloat("Last Y-Coord", 0);
		}
		ocMainCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		sbMain.setProjectionMatrix(ocMainCam.combined);
		ocMainCam.position.x = MathUtils.clamp(ocMainCam.position.x, 0, Gdx.graphics.getWidth());
		ocMainCam.position.y = MathUtils.clamp(ocMainCam.position.y, 0, Gdx.graphics.getHeight());
		ocMainCam.update();

		orthotmrRenderer.setView(ocMainCam);
		orthotmrRenderer.render();
		ocMainCam.update();

		sbMain.begin();
		//sbMain.draw(txBackground,0,0);
		sbMain.draw(trCurrentFrame,(int)fSpriteX, (int)fSpriteY);
		sbMain.end();
		prefCoords.flush();
	}
}
