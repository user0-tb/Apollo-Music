/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.andrew.apollo.ui.fragments.phone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.andrew.apollo.R;
import com.andrew.apollo.adapters.PagerAdapter;
import com.andrew.apollo.adapters.PagerAdapter.MusicFragments;
import com.andrew.apollo.utils.MusicUtils;
import com.andrew.apollo.utils.NavUtils;
import com.andrew.apollo.utils.PreferenceUtils;
import com.andrew.apollo.utils.SortOrder;
import com.andrew.apollo.utils.ThemeUtils;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.OnCenterItemClickListener;

/**
 * This class is used to hold the {@link ViewPager} used for swiping between the
 * playlists, recent, artists, albums, songs, and genre {@link Fragment}
 * s for phones.
 * <p>
 * #note  The reason the sort orders are taken care of in this fragment rather
 * than the individual fragments is to keep from showing all of the menu
 * items on tablet interfaces. That being said, I have a tablet interface
 * worked out, but I'm going to keep it in the Play Store version of
 * Apollo for a couple of weeks or so before merging it with CM.
 *
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class MusicBrowserPhoneFragment extends Fragment implements OnCenterItemClickListener {

    /**
     * index of {@link com.andrew.apollo.ui.fragments.RecentFragment}
     */
    private static final int RECENT_INDEX = 1;

    /**
     * index of {@link com.andrew.apollo.ui.fragments.ArtistFragment}
     */
    private static final int ARTIST_INDEX = 2;

    /**
     * index of {@link com.andrew.apollo.ui.fragments.SongFragment}
     */
    private static final int TRACKS_INDEX = 3;

    /**
     * index of {@link com.andrew.apollo.ui.fragments.AlbumFragment}
     */
    private static final int ALBUMS_INDEX = 4;

    /**
     * Pager
     */
    private ViewPager mViewPager;

    /**
     * VP's adapter
     */
    private PagerAdapter mPagerAdapter;

    /**
     * Theme resources
     */
    private ThemeUtils mResources;

    private PreferenceUtils mPreferences;

    /**
     * Empty constructor as per the {@link Fragment} documentation
     */
    public MusicBrowserPhoneFragment() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the preferences
        mPreferences = PreferenceUtils.getInstance(requireContext());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // The View for the fragment's UI
        View rootView = inflater.inflate(R.layout.fragment_music_browser_phone, container, false);
        // Initialize the adapter
        mPagerAdapter = new PagerAdapter(requireActivity());
        for (MusicFragments mFragment : MusicFragments.values()) {
            mPagerAdapter.add(mFragment.getFragmentClass(), null);
        }
        // Initialize the ViewPager
        mViewPager = rootView.findViewById(R.id.fragment_home_phone_pager);
        // Attach the adapter
        mViewPager.setAdapter(mPagerAdapter);
        // Offscreen pager loading limit
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount() - 1);
        // Start on the last page the user was on
        mViewPager.setCurrentItem(mPreferences.getStartPage());
        // Initialize the TPI
        TitlePageIndicator pageIndicator = rootView.findViewById(R.id.fragment_home_phone_pager_titles);
        // Attach the ViewPager
        pageIndicator.setViewPager(mViewPager);
        // Scroll to the current artist, album, or song
        pageIndicator.setOnCenterItemClickListener(this);
        return rootView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initialze the theme resources
        mResources = new ThemeUtils(requireActivity());
        // Enable the options menu
        setHasOptionsMenu(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        // Save the last page the use was on
        mPreferences.setStartPage(mViewPager.getCurrentItem());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem favorite = menu.findItem(R.id.menu_favorite);
        mResources.setFavoriteIcon(favorite);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Favorite action
        inflater.inflate(R.menu.favorite, menu);
        // Shuffle all
        inflater.inflate(R.menu.shuffle, menu);
        // Sort orders
        switch (mViewPager.getCurrentItem()) {
            case RECENT_INDEX:
                inflater.inflate(R.menu.view_as, menu);
                break;

            case ARTIST_INDEX:
                inflater.inflate(R.menu.artist_sort_by, menu);
                inflater.inflate(R.menu.view_as, menu);
                break;

            case TRACKS_INDEX:
                inflater.inflate(R.menu.song_sort_by, menu);
                break;

            case ALBUMS_INDEX:
                inflater.inflate(R.menu.album_sort_by, menu);
                inflater.inflate(R.menu.view_as, menu);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int vId = item.getItemId();
        if (vId == R.id.menu_shuffle) {
            // Shuffle all the songs
            MusicUtils.shuffleAll(requireContext());
        } else if (vId == R.id.menu_favorite) {
            // Toggle the current track as a favorite and update the menu item
            MusicUtils.toggleFavorite();
            requireActivity().invalidateOptionsMenu();
        } else if (vId == R.id.menu_sort_by_az) {
            if (mViewPager.getCurrentItem() == ARTIST_INDEX) {
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_A_Z);
                getCallback(ARTIST_INDEX).refresh();
            } else if (mViewPager.getCurrentItem() == ALBUMS_INDEX) {
                mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_A_Z);
                getCallback(ALBUMS_INDEX).refresh();
            } else if (mViewPager.getCurrentItem() == TRACKS_INDEX) {
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z);
                getCallback(TRACKS_INDEX).refresh();
            }
        } else if (vId == R.id.menu_sort_by_za) {
            if (mViewPager.getCurrentItem() == ARTIST_INDEX) {
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_Z_A);
                getCallback(ARTIST_INDEX).refresh();
            } else if (mViewPager.getCurrentItem() == ALBUMS_INDEX) {
                mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_Z_A);
                getCallback(ALBUMS_INDEX).refresh();
            } else if (mViewPager.getCurrentItem() == TRACKS_INDEX) {
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_Z_A);
                getCallback(TRACKS_INDEX).refresh();
            }
        } else if (vId == R.id.menu_sort_by_artist) {
            if (mViewPager.getCurrentItem() == ALBUMS_INDEX) {
                mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_ARTIST);
                getCallback(ALBUMS_INDEX).refresh();
            } else if (mViewPager.getCurrentItem() == TRACKS_INDEX) {
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ARTIST);
                getCallback(TRACKS_INDEX).refresh();
            }
        } else if (vId == R.id.menu_sort_by_album) {
            if (mViewPager.getCurrentItem() == TRACKS_INDEX) {
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ALBUM);
                getCallback(TRACKS_INDEX).refresh();
            }
        } else if (vId == R.id.menu_sort_by_year) {
            if (mViewPager.getCurrentItem() == ALBUMS_INDEX) {
                mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_YEAR);
                getCallback(ALBUMS_INDEX).refresh();
            } else if (mViewPager.getCurrentItem() == TRACKS_INDEX) {
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_YEAR);
                getCallback(TRACKS_INDEX).refresh();
            }
        } else if (vId == R.id.menu_sort_by_duration) {
            if (mViewPager.getCurrentItem() == TRACKS_INDEX) {
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DURATION);
                getCallback(TRACKS_INDEX).refresh();
            }
        } else if (vId == R.id.menu_sort_by_number_of_songs) {
            if (mViewPager.getCurrentItem() == ARTIST_INDEX) {
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS);
                getCallback(ALBUMS_INDEX).refresh();
            } else if (mViewPager.getCurrentItem() == ALBUMS_INDEX) {
                mPreferences.setAlbumSortOrder(SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS);
                getCallback(ALBUMS_INDEX).refresh();
            }
        } else if (vId == R.id.menu_sort_by_number_of_albums) {
            if (mViewPager.getCurrentItem() == ARTIST_INDEX) {
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS);
                getCallback(ARTIST_INDEX).refresh();
            }
        } else if (vId == R.id.menu_sort_by_filename) {
            if (mViewPager.getCurrentItem() == TRACKS_INDEX) {
                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_FILENAME);
                getCallback(TRACKS_INDEX).refresh();
            }
        } else if (vId == R.id.menu_view_as_simple) {
            if (mViewPager.getCurrentItem() == RECENT_INDEX) {
                mPreferences.setRecentLayout("simple");
            } else if (mViewPager.getCurrentItem() == ARTIST_INDEX) {
                mPreferences.setArtistLayout("simple");
            } else if (mViewPager.getCurrentItem() == ALBUMS_INDEX) {
                mPreferences.setAlbumLayout("simple");
            }
            NavUtils.goHome(requireActivity());
        } else if (vId == R.id.menu_view_as_detailed) {
            if (mViewPager.getCurrentItem() == RECENT_INDEX) {
                mPreferences.setRecentLayout("detailed");
            } else if (mViewPager.getCurrentItem() == ARTIST_INDEX) {
                mPreferences.setArtistLayout("detailed");
            } else if (mViewPager.getCurrentItem() == ALBUMS_INDEX) {
                mPreferences.setAlbumLayout("detailed");
            }
            NavUtils.goHome(requireActivity());
        } else if (vId == R.id.menu_view_as_grid) {
            if (mViewPager.getCurrentItem() == RECENT_INDEX) {
                mPreferences.setRecentLayout("grid");
            } else if (mViewPager.getCurrentItem() == ARTIST_INDEX) {
                mPreferences.setArtistLayout("grid");
            } else if (mViewPager.getCurrentItem() == ALBUMS_INDEX) {
                mPreferences.setAlbumLayout("grid");
            }
            NavUtils.goHome(requireActivity());
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCenterItemClick(int position) {
        getCallback(position).scrollToCurrent();
    }

    /**
     * return callbacks to the fragments
     *
     * @param index index of the fragment
     * @return fragment
     */
    private BrowserCallback getCallback(int index) {
        return (BrowserCallback) mPagerAdapter.getFragment(index);
    }

    /**
     * reload all sub fragments
     */
    public void refresh() {
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            getCallback(i).refresh();
        }
    }

    /**
     * Callbacks to interact with fragments
     */
    public interface BrowserCallback {

        /**
         * reload content after change
         */
        void refresh();

        /**
         * scroll to current item
         */
        void scrollToCurrent();
    }
}