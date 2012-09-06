package com.otgard.andengine101;


import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.svg.opengl.texture.atlas.bitmap.SVGBitmapTextureAtlasTextureRegionFactory;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.ease.EaseBackIn;
import org.andengine.util.modifier.ease.EaseElasticOut;

import android.view.Display;

public class MainActivity extends BaseGameActivity {
	private Camera camera;

	private int xpos;
	private int ypos;

	/** 1 */
	@Override
	public EngineOptions onCreateEngineOptions() {
		final Display display = getWindowManager().getDefaultDisplay();

		int cameraWidth = display.getWidth() > display.getHeight() ? display.getWidth() : display.getHeight();
		int cameraHeight = display.getWidth() < display.getHeight() ? display.getWidth() : display.getHeight();

		camera = new Camera(0, 0, cameraWidth, cameraHeight);
		EngineOptions options = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FixedResolutionPolicy(cameraWidth, cameraHeight), camera);
		return options;
	}

	/** 2 */
	@Override
	public void onCreateResources(final OnCreateResourcesCallback callback) throws Exception {
		callback.onCreateResourcesFinished();
	}

	/** 3 */
	@Override
	public void onCreateScene(final OnCreateSceneCallback callback) throws Exception {

		// create scene with dark gray background
		Scene scene = new Scene();
		scene.setBackground(new Background(new Color(0.3f, 0.3f, 0.3f)));
		callback.onCreateSceneFinished(scene);
	}

	/** 4 */
	@Override
	public void onPopulateScene(final Scene scene, final OnPopulateSceneCallback callback) throws Exception {

		// sprite size
		final int width = 150;
		final int height = 150;

		// calculate center of screen
		Display display = getWindowManager().getDefaultDisplay();
		int cameraWidth = display.getWidth() > display.getHeight() ? display.getWidth() : display.getHeight();
		int cameraHeight = display.getWidth() < display.getHeight() ? display.getWidth() : display.getHeight();

		xpos = cameraWidth / 2 - width / 2;
		ypos = cameraHeight / 2 - height / 2;;

		// create bitmap
		SVGBitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		BitmapTextureAtlas bitmap = new BitmapTextureAtlas(getTextureManager(), width, height, TextureOptions.BILINEAR);

		// create and load sprite on texture region
		ITextureRegion textureRegion = SVGBitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmap, this, "level-star.svg", width, height, 0, 0);
		getTextureManager().loadTexture(bitmap);

		// put sprite on scene
		final Sprite star = new Sprite(xpos, ypos, textureRegion, getVertexBufferObjectManager()) {

			@Override
			public boolean onAreaTouched(TouchEvent event, float touchAreaLocalX, float touchAreaLocalY) {
				handleTouch(this);
				return true;
			}
		};

		scene.attachChild(star);
		scene.registerTouchArea(star);

		callback.onPopulateSceneFinished();
	}

	// called by @Override Sprite.onAreaTouched()
	private void handleTouch(final Sprite star) {
		final IEntityModifier modifiers = new SequenceEntityModifier(
				new MoveYModifier(1f, star.getY(), -star.getHeight(), EaseBackIn.getInstance()),
				new DelayModifier(0.5f),
				new MoveModifier(2f, -star.getWidth(), xpos, ypos, ypos, EaseElasticOut.getInstance()));

		star.registerEntityModifier(modifiers);
	}
}
