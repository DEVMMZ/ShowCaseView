package ir.smartdevelop.eram.showcaseview;

import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.config.PointerType;

public class MainActivity extends AppCompatActivity {

    View view1;
    View view2;
    View view3;
    View view4;
    View view5;
    View view6;
    private GuideView mGuideView;
    private GuideView.Builder builder;
    private List<View> guideSequence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);
        view6 = findViewById(R.id.view6);
        guideSequence = Arrays.asList(view1, view2, view3, view4, view5, view6);

        builder = new GuideView.Builder(this)
                .setTitle("Guide Title Text")
                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
                .setTypeFace(Typeface.SANS_SERIF)
                .setTitleTextStyle(Typeface.BOLD)
                .setContentTextColor(Color.DKGRAY)
                .setMessageBackgroundColor(Color.WHITE)
                .setOverlayColor(0xB3000000)
                .setLineIndicatorColor(Color.WHITE)
                .setCircleIndicatorColor(Color.WHITE)
                .setCircleInnerIndicatorColor(Color.LTGRAY)
                .setGravity(Gravity.center)
                .setDismissType(DismissType.anywhere)
                .setPointerType(PointerType.circle)
                .setShowSkipButton(true)
                .setSkipButtonBackgroundDrawable(
                        ContextCompat.getDrawable(this, android.R.drawable.ic_menu_close_clear_cancel)
                )
                .setTargetView(guideSequence.get(0))
                .setSkipListener(view -> {
                    // Skip ends the guide sequence without advancing to the next step.
                })
                .setGuideListener(view -> {
                    int nextIndex = guideSequence.indexOf(view) + 1;
                    if (nextIndex < guideSequence.size()) {
                        showGuideAt(nextIndex);
                    }
                });

        showGuideAt(0);

        updatingForDynamicLocationViews();
    }

    private void updatingForDynamicLocationViews() {
        view4.setOnFocusChangeListener((view, b) -> mGuideView.updateGuideViewLocation());
    }

    private void showGuideAt(int index) {
        mGuideView = builder.setTargetView(guideSequence.get(index)).build();
        mGuideView.show();
    }

}
