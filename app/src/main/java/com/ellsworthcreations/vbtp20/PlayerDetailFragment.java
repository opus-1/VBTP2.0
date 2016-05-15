package com.ellsworthcreations.vbtp20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
/**
 * A fragment representing a single Player detail screen.
 * This fragment is either contained in a {@link PlayerListActivity}
 * in two-pane mode (on tablets) or a {@link PlayerDetailActivity}
 * on handsets.
 */
public class PlayerDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "player_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Player player;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayerDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            player = VBTP.PlayerDB().getPlayerByID(getArguments().getInt(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(player.getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player_detail, container, false);

        if (player != null) {
            RadioGroup rg = ((RadioGroup) rootView.findViewById(R.id.gender_selection));
            if (player.isFemale()) {
                rg.check(R.id.gender_female_id);
            } else {
                rg.check(R.id.gender_male_id);
            }

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup rGroup, int checkedId) {
                    // This will get the radiobutton that has changed in its check state
                    RadioButton checkedRadioButton = (RadioButton) rGroup.findViewById(checkedId);
                    if (checkedId == R.id.gender_female_id) {
                        player.setGender(Player.Gender.FEMALE);
                    } else {
                        player.setGender(Player.Gender.MALE);
                    }

                    player.save();
                }
            });

            Switch active = (Switch) rootView.findViewById(R.id.player_is_active_id);
            active.setChecked(player.isActive());
            active.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton cb, boolean checked) {
                    player.setActive(checked);
                    player.save();
                }
            });

            TextView name = (TextView) rootView.findViewById(R.id.playerNameEditText);
            name.setText(player.getName());
            name.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    String[] names = s.toString().split(" ", 2);
                    player.setName(names[0], names[1]);
                    player.save();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });

            LinearLayout skillsContainer = (LinearLayout) rootView.findViewById(R.id.skillsLayout);
            for (int i = 0; i < Settings.colSkills.length; i++) {
                String skill = Settings.colSkills[i];
                Log.d("SKILL", skill);
                LinearLayout skillView = (LinearLayout) inflater.inflate(R.layout.player_skill, null);
                ((TextView) skillView.findViewWithTag("SkillName")).setText(skill);
                RatingBar rb = (RatingBar) skillView.findViewWithTag("SkillRating");
                rb.setTag(skill);
                rb.setRating((float) player.getSkill(skill));
                rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar b, float v, boolean fromuser) {
                        if (fromuser) {
                            player.setSkill((String) b.getTag().toString(), (int) v);
                            player.save();
                        }
                    }
                });
                skillsContainer.addView(skillView);
            }
        }

        return rootView;
    }
}
