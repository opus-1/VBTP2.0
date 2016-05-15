package com.ellsworthcreations.vbtp20;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

/**
 * An activity representing a list of Players. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PlayerDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PlayerListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addPlayerFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player p = new Player(-1, "Doe", "John", true, VBTP.PlayerDB());
                p.save();
                if(p.getPlayerID() > 0) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, PlayerDetailActivity.class);
                    intent.putExtra(PlayerDetailFragment.ARG_ITEM_ID, p.getPlayerID());
                    context.startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Could not create new player.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        View recyclerView = findViewById(R.id.player_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.player_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d("VBTP.PlayerDB", VBTP.PlayerDB(this).toString());
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(VBTP.PlayerDB(this).getAllPlayers()));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Player> players;

        public SimpleItemRecyclerViewAdapter(Player[] items) {
            players = Arrays.asList(items);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.player_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = players.get(position);
            holder.mIdView.setText(players.get(position).getLastName() + ", " + players.get(position).getFirstName());
            holder.mContentView.setText(Integer.toString(players.get(position).getSkills().cumulativeSkills()));
            holder.mPresent.setChecked(players.get(position).isActive());
            final Player player = players.get(position);
            holder.mPresent.setOnCheckedChangeListener( new Switch.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton cb, boolean v) {
                    player.setActive(v); player.save();

                }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(PlayerDetailFragment.ARG_ITEM_ID, holder.mItem.getPlayerID());
                    PlayerDetailFragment fragment = new PlayerDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.player_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PlayerDetailActivity.class);
                    intent.putExtra(PlayerDetailFragment.ARG_ITEM_ID, holder.mItem.getPlayerID());
                    context.startActivity(intent);
                }
                }
            });
        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final Switch mPresent;
            public Player mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.player_list_column_1);
                mContentView = (TextView) view.findViewById(R.id.player_list_column_2);
                mPresent = (Switch) view.findViewById(R.id.playerIsPresent);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
