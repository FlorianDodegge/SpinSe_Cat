package com.example.floriandodegge.spinsecat;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

import java.util.Arrays;
import java.util.Date;


public class MainActivity extends FragmentActivity {

    private ImageView img;
    private GestureDetector gd;
    private float xDown, xUp, yDown, yUp, deltaX, deltaY, rotation;
    private Animation rotationRight, rotationLeft;
    private int minSwipeDelta;        //min Distanz
    public int drinkoMeter = 0;

    //FB
    private final String PENDING_ACTION_BUNDLE_KEY = "com.example.floriandodegge.spinsecat,PendingAction";
    private Button postStatusUpdateButton;
    private Button drinkUpdateButton;
    private LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    private TextView greeting;
    private PendingAction pendingAction = PendingAction.NONE;
    private ViewGroup controlsContainer;
    private GraphUser user;
    private boolean canPresentShareDialog;
    private static final String PERMISSION = "publish_actions";
    private TextView textBelow;
    private TextView textView;
    private TextView drinkoMeterView;
    private int randomId;


    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
        @Override
        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
        }

        @Override
        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
            Log.d("HelloFacebook", "Success!");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        img = (ImageView) findViewById(R.id.image);
        ActionBar actionbar = getActionBar();
        actionbar.hide();

        rotationRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right);
        rotationLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left);

        gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener());

        ViewConfiguration config = ViewConfiguration.get(this);         //für Standardkonfigurationen (Timeouts, Größen, Distanzen)
        minSwipeDelta = config.getScaledPagingTouchSlop();              //Distanz in Pixel

        //FB

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
            pendingAction = PendingAction.valueOf(name);
        }

        setContentView(R.layout.activitiy_facebook_post);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                MainActivity.this.user = user;
                updateUI();
                // It's possible that we were waiting for this.user to be populated in order to post a
                // status update.
                handlePendingAction();
            }
        });
        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        greeting = (TextView) findViewById(R.id.greeting);

        textBelow = (TextView) findViewById(R.id.textBelow);
        textView = (TextView) findViewById(R.id.textView);
        drinkoMeterView = (TextView) findViewById(R.id.drinkoMeter);

        drinkUpdateButton = (Button) findViewById(R.id.drinkUpdateButton);
        drinkUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                drinkoMeter++;

                drinkoMeterView.setText("DrinkoMeter Pegel: "+drinkoMeter);
                if(drinkoMeter == 5){
                    Toast.makeText(getApplicationContext(), "Heut läufts bei dir!", Toast.LENGTH_SHORT).show();
                }
                if(drinkoMeter == 10){
                    Toast.makeText(getApplicationContext(), "Junge, Autofahren ist heut gestorben!", Toast.LENGTH_SHORT).show();
                }
                if(drinkoMeter == 15){
                    Toast.makeText(getApplicationContext(), "Wenn das dein Opa noch sehen könnte!", Toast.LENGTH_SHORT).show();
                }
                if(drinkoMeter == 20){
                    Toast.makeText(getApplicationContext(), "Das machst du nicht zum ersten Mal, was!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
        greeting = (TextView) findViewById(R.id.greeting);

        postStatusUpdateButton = (Button) findViewById(R.id.postStatusUpdateButton);
        postStatusUpdateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickPostStatusUpdate();
            }
        });


        controlsContainer = (ViewGroup) findViewById(R.id.main_ui_container);

        final FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            // If we're being re-created and have a fragment, we need to a) hide the main UI controls and
            // b) hook up its listeners again.
            controlsContainer.setVisibility(View.GONE);
        }

        // Listen for changes in the back stack so we know if a fragment got popped off because the user
        // clicked the back button.
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fm.getBackStackEntryCount() == 0) {
                    // We need to re-show our UI.
                    controlsContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        // Can we present the share dialog for regular links?
        canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
                FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
        // Can we present the share dialog for photos?

    }


    public boolean onTouchEvent(MotionEvent event) {

        gd.onTouchEvent(event);

        boolean result = false;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                xDown = event.getX();
                yDown = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                xUp = event.getX();
                yUp = event.getY();

                deltaX = xUp - xDown;
                deltaY = yUp - yDown;


                if (Math.abs(deltaX) > Math.abs(deltaY)) {    //horizontaler Swipe
                    if (Math.abs(deltaX) > minSwipeDelta) {   //Gültigkeit bzgl Standardeinstellungen prüfen
                        if (deltaX < 0) {
                            Log.i("SWIPE", "right");
                            img.startAnimation(rotationRight);

                        } else {
                            Log.i("SWIPE", "left");
                            img.startAnimation(rotationLeft);
                        }
                    }
                    result = true;
                } else if (Math.abs(deltaY) > minSwipeDelta) {   //vertikaler Swipe
                    if (deltaY < 0) {
                        Log.i("SWIPE", "left");
                        img.startAnimation(rotationLeft);
                    } else {
                        Log.i("SWIPE", "right");
                        img.startAnimation(rotationRight);
                    }
                }

                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_activitiy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);

        updateUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (pendingAction != PendingAction.NONE &&
                (exception instanceof FacebookOperationCanceledException ||
                        exception instanceof FacebookAuthorizationException)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.cancelled)
                    .setMessage(R.string.permission_not_granted)
                    .setPositiveButton(R.string.ok, null)
                    .show();
            pendingAction = PendingAction.NONE;
        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
            handlePendingAction();
        }
        updateUI();
    }

    private void updateUI() {
        Session session = Session.getActiveSession();
        boolean enableButtons = (session != null && session.isOpened());

        if (enableButtons && user != null) {
            profilePictureView.setProfileId(user.getId());
            greeting.setText(getString(R.string.hello_user, user.getFirstName()));
            textBelow.setText("Spin se Cat!");
            textView.setText("Lass die Spiele beginnen");

        } else {
            profilePictureView.setProfileId(null);
            greeting.setText(null);
            textView.setText("Willkommen bei Spin Se Cat");
            textBelow.setText("Bitte bei Facebook einloggen!");
        }
        postStatusUpdateButton.setEnabled(enableButtons || canPresentShareDialog);

    }

    @SuppressWarnings("incomplete-switch")
    private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {

            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }

    private interface GraphObjectWithId extends GraphObject {
        String getId();
    }

    private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
        String title = null;
        String alertMessage = null;
        if (error == null) {
            title = getString(R.string.success);
            String id = result.cast(GraphObjectWithId.class).getId();
            alertMessage = getString(R.string.successfully_posted_post, message, id);
        } else {
            title = getString(R.string.error);
            alertMessage = error.getErrorMessage();
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }

    private FacebookDialog.ShareDialogBuilder createShareDialogBuilderForLink() {

        //test ID
        randomId = 1;
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        Post post = db.getPostById(randomId);

        return new FacebookDialog.ShareDialogBuilder(this)
                .setName(post.name)
                .setDescription(post.description)
                .setLink(post.link);

    }

    private void postStatusUpdate() {
        if (canPresentShareDialog) {
            FacebookDialog shareDialog = createShareDialogBuilderForLink().build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        } else if (user != null && hasPublishPermission()) {
            final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
            Request request = Request
                    .newStatusUpdateRequest(Session.getActiveSession(), message, new Request.Callback() {
                        @Override
                        public void onCompleted(Response response) {
                            showPublishResult(message, response.getGraphObject(), response.getError());
                        }
                    });
            request.executeAsync();
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private FacebookDialog.PhotoShareDialogBuilder createShareDialogBuilderForPhoto(Bitmap... photos) {
        return new FacebookDialog.PhotoShareDialogBuilder(this)
                .addPhotos(Arrays.asList(photos));
    }


    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    private boolean hasPublishPermission() {
        Session session = Session.getActiveSession();
        return session != null && session.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action, boolean allowNoSession) {
        Session session = Session.getActiveSession();
        if (session != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else if (session.isOpened()) {
                // We need to get new permissions, then complete the action when we get called back.
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
                return;
            }
        }

        if (allowNoSession) {
            pendingAction = action;
            handlePendingAction();
        }
    }

}

