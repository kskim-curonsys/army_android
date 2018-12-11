package com.curonsys.army_android.arcore;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.curonsys.army_android.R;
import com.curonsys.army_android.util.SnackbarHelper;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.ExternalTexture;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This is an example activity that shows how to display a video with chroma key filtering in
 * Sceneform.
 */
public class ChromaKeyVideoActivity extends AppCompatActivity {
  private static final String TAG = ChromaKeyVideoActivity.class.getSimpleName();
  private static final double MIN_OPENGL_VERSION = 3.0;
    private static final String DEFAULT_IMAGE_NAME = "default.jpg";
    private static final String SAMPLE_IMAGE_DATABASE = "sample_database.imgdb";
    private static final boolean USE_SINGLE_IMAGE = false;

    private ArFragment arFragment;

  @Nullable private ModelRenderable videoRenderable;
  private MediaPlayer mediaPlayer;

  // The color to filter out of the video.
  private static final Color CHROMA_KEY_COLOR = new Color(0.1843f, 1.0f, 0.098f);

  // Controls the height of the video in world space.
  private static final float VIDEO_HEIGHT_METERS = 0.85f;

  private ImageView fitToScanView;

  ExternalTexture mTexture;
  private final Map<AugmentedImage, AugmentedImageNode> augmentedImageMap = new HashMap<>();

    @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  // CompletableFuture requires api level 24
  // FutureReturnValueIgnored is not valid
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (!checkIsSupportedDeviceOrFinish(this)) {
      return;
    }

    setContentView(R.layout.activity_arcore_video);

    arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

    fitToScanView = findViewById(R.id.image_view_fit_to_scan);

        // Create an ExternalTexture for displaying the contents of the video.
    mTexture = new ExternalTexture();

    // Create an Android MediaPlayer to capture the video on the external texture's surface.
    mediaPlayer = MediaPlayer.create(this, R.raw.lion_chroma);
    //mediaPlayer = MediaPlayer.create(this, R.raw.camera_test);
    mediaPlayer.setSurface(mTexture.getSurface());
    mediaPlayer.setLooping(true);

    // Create a renderable with a material that has a parameter of type 'samplerExternal' so that
    // it can display an ExternalTexture. The material also has an implementation of a chroma key
    // filter.
    ModelRenderable.builder()
        .setSource(this, R.raw.chroma_key_video)
        .build()
        .thenAccept(
            renderable -> {
              videoRenderable = renderable;
              renderable.getMaterial().setExternalTexture("videoTexture", mTexture);
              renderable.getMaterial().setFloat4("keyColor", CHROMA_KEY_COLOR);
            })
        .exceptionally(
            throwable -> {
              Toast toast =
                  Toast.makeText(this, "Unable to load video renderable", Toast.LENGTH_LONG);
              toast.setGravity(Gravity.CENTER, 0, 0);
              toast.show();
              return null;
            });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (videoRenderable == null) {
                        return;
                    }

                    // Create the Anchor.
                    Pose hitpose = hitResult.getHitPose();

                    float[] translation = new float[3];
                    translation = hitpose.getTranslation();

                    float[] quaternion = new float[4];
                    quaternion = hitpose.getRotationQuaternion();

                    String posestr = String.format(Locale.ENGLISH, "t:[x:%.3f, y:%.3f, z:%.3f], q:[x:%.2f, y:%.2f, z:%.2f, w:%.2f]", translation[0], translation[1], translation[2], quaternion[0], quaternion[1], quaternion[2], quaternion[3]);

                    Log.e(TAG, "HitResult Pose: " + hitpose.toString());
                    Log.e(TAG, posestr);

                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create a node to render the video and add it to the anchor.
                    Node videoNode = new Node();
                    videoNode.setParent(anchorNode);

                    // Set the scale of the node so that the aspect ratio of the video is correct.
                    float videoWidth = mediaPlayer.getVideoWidth();
                    float videoHeight = mediaPlayer.getVideoHeight();
                    videoNode.setLocalScale(
                            new Vector3(
                                    VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), VIDEO_HEIGHT_METERS, 1.0f));

                    // Start playing the video when the first node is placed.
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();

                        // Wait to set the renderable until the first frame of the  video becomes available.
                        // This prevents the renderable from briefly appearing as a black quad before the video
                        // plays.
                        mTexture
                                .getSurfaceTexture()
                                .setOnFrameAvailableListener(
                                        (SurfaceTexture surfaceTexture) -> {
                                            videoNode.setRenderable(videoRenderable);
                                            mTexture.getSurfaceTexture().setOnFrameAvailableListener(null);
                                        });
                    } else {
                        videoNode.setRenderable(videoRenderable);
                    }
                });

    //arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
  }

    @Override
    protected void onResume() {
        super.onResume();
        if (augmentedImageMap.isEmpty()) {
            fitToScanView.setVisibility(View.VISIBLE);
        }
    }

    @Override
  public void onDestroy() {
    super.onDestroy();

    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
    }
  }

  /**
   * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
   * on this device.
   *
   * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
   *
   * <p>Finishes the activity if Sceneform can not run
   */
  public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
    if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
      Log.e(TAG, "Sceneform requires Android N or later");
      Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
      activity.finish();
      return false;
    }
    String openGlVersionString =
        ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
            .getDeviceConfigurationInfo()
            .getGlEsVersion();
    if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
      Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
      Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
          .show();
      activity.finish();
      return false;
    }
    return true;
  }

    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame or ARCore is not tracking yet, just return.
        if (frame == null || frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
                    String text = "Detected Image " + augmentedImage.getIndex();
                    SnackbarHelper.getInstance().showMessage(this, text);
                    break;

                case TRACKING:
                    // Have to switch to UI Thread to update View.
                    fitToScanView.setVisibility(View.GONE);

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        // Create the Anchor.

                        // check pose
                        Pose imgpose = augmentedImage.getCenterPose();

                        float[] translation = new float[3];
                        translation = imgpose.getTranslation();

                        float[] quaternion = new float[4];
                        quaternion = imgpose.getRotationQuaternion();

                        String posestr = String.format(Locale.ENGLISH, "t:[x:%.3f, y:%.3f, z:%.3f], q:[x:%.2f, y:%.2f, z:%.2f, w:%.2f]", translation[0], translation[1], translation[2], quaternion[0], quaternion[1], quaternion[2], quaternion[3]);

                        Log.e(TAG, "Augmented Image Pose: " + imgpose.toString());
                        Log.e(TAG, posestr);

                        Anchor anchor = augmentedImage.createAnchor(augmentedImage.getCenterPose());
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());

                        // Create a node to render the video and add it to the anchor.
                        Node videoNode = new Node();
                        videoNode.setParent(anchorNode);

                        // Set the scale of the node so that the aspect ratio of the video is correct.
                        float videoWidth = mediaPlayer.getVideoWidth();
                        float videoHeight = mediaPlayer.getVideoHeight();
                        videoNode.setLocalScale(
                                new Vector3(
                                        VIDEO_HEIGHT_METERS * (videoWidth / videoHeight), VIDEO_HEIGHT_METERS, 1.0f));

                        // Start playing the video when the first node is placed.
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();

                            // Wait to set the renderable until the first frame of the  video becomes available.
                            // This prevents the renderable from briefly appearing as a black quad before the video
                            // plays.
                            mTexture
                                    .getSurfaceTexture()
                                    .setOnFrameAvailableListener(
                                            (SurfaceTexture surfaceTexture) -> {
                                                videoNode.setRenderable(videoRenderable);
                                                mTexture.getSurfaceTexture().setOnFrameAvailableListener(null);
                                            });
                        } else {
                            videoNode.setRenderable(videoRenderable);
                        }
                    }
                    break;

                case STOPPED:
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }
    }

}
