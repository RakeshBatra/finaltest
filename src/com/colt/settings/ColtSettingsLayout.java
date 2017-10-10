/*
 * Copyright (C) 2017 ColtOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.colt.settings;

import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.app.Fragment;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.app.FragmentManager;
import android.os.Handler;
import android.os.UserHandle;
import android.os.Bundle;
import android.preference.Preference.OnPreferenceChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.support.v13.app.FragmentPagerAdapter;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.provider.Settings;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.net.Uri;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.colt.settings.fragments.StatusBarSettings;
import com.colt.settings.fragments.QuickSettings;
import com.colt.settings.fragments.BlurPersonalizations;
import com.colt.settings.fragments.ButtonSettings;
import com.colt.settings.fragments.RecentSettings;
import com.colt.settings.fragments.NavbarSettings;
import com.colt.settings.fragments.LockScreenSettings;
import com.colt.settings.fragments.PowerMenuSettings;
import com.colt.settings.fragments.AnimationSettings;
import com.colt.settings.fragments.MiscSettings;
import com.colt.settings.fragments.About;
import com.dirtyunicorns.dutweaks.viewpager.transforms.*;

public class ColtSettingsLayout extends SettingsPreferenceFragment {

    private static final String TAG = "ColtSettingsLayout";
    ViewPager mViewPager;
    ViewGroup mContainer;
    PagerSlidingTabStrip mTabs;
    SectionsPagerAdapter mSectionsPagerAdapter;
    protected Context mContext;
    private LinearLayout mLayout;
    private SettingsObserver mSettingsObserver;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;
        View view = inflater.inflate(R.layout.colt_settings, container, false);
        mLayout = (LinearLayout) view.findViewById(R.id.colt_content);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
	mSettingsObserver = new SettingsObserver(new Handler());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabs.setViewPager(mViewPager);
        mContext = getActivity().getApplicationContext();
        ContentResolver resolver = getActivity().getContentResolver();
	mSettingsObserver.observe();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.COLT;
    }

    class SectionsPagerAdapter extends FragmentPagerAdapter {

        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            frags[0] = new StatusBarSettings();
            frags[1] = new QuickSettings();
            frags[2] = new BlurPersonalizations();
            frags[3] = new ButtonSettings();
            frags[4] = new RecentSettings();
            frags[5] = new NavbarSettings();
            frags[6] = new LockScreenSettings();
            frags[7] = new PowerMenuSettings();
            frags[8] = new AnimationSettings();
            frags[9] = new MiscSettings();
            frags[10] = new About();
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private String[] getTitles() {
        String titleString[];
        titleString = new String[] {
            getString(R.string.statusbar_settings_title),
            getString(R.string.quicksettings_title),
            getString(R.string.settings_blur_cat),
            getString(R.string.button_title),
            getString(R.string.recent_settings_title),
            getString(R.string.navbar_settings_title),
            getString(R.string.lockscreen_settings_title),
            getString(R.string.powermenu_title),
            getString(R.string.animation_title),
            getString(R.string.misc_settings_title),
            getString(R.string.about_colt)
        };
        return titleString;
	}

	private class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            ContentResolver resolver = mContext.getContentResolver();
            resolver.registerContentObserver(Settings.System.getUriFor(
                    Settings.System.DIRTY_TWEAKS_TABS_EFFECT),
                    false, this, UserHandle.USER_ALL);
            update();
        }

        void unobserve() {
            ContentResolver resolver = mContext.getContentResolver();
            resolver.unregisterContentObserver(this);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            update();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            update();
        }

        public void update() {
            ContentResolver resolver = mContext.getContentResolver();
            int effect = Settings.System.getIntForUser(resolver,
                Settings.System.DIRTY_TWEAKS_TABS_EFFECT, 0,
                UserHandle.USER_CURRENT);
            switch (effect) {
                case 0:
                    mViewPager.setPageTransformer(true, new DefaultTransformer());
                    break;
                case 1:
                   mViewPager.setPageTransformer(true, new AccordionTransformer());
                    break;
                case 2:
                    mViewPager.setPageTransformer(true, new BackgroundToForegroundTransformer());
                    break;
                case 3:
                    mViewPager.setPageTransformer(true, new CubeInTransformer());
                    break;
                case 4:
                    mViewPager.setPageTransformer(true, new CubeOutTransformer());
                    break;
                case 5:
                    mViewPager.setPageTransformer(true, new DepthPageTransformer());
                    break;
                case 6:
                    mViewPager.setPageTransformer(true, new FlipHorizontalTransformer());
                    break;
                case 7:
                    mViewPager.setPageTransformer(true, new FlipVerticalTransformer());
                    break;
                case 8:
                    mViewPager.setPageTransformer(true, new ForegroundToBackgroundTransformer());
                    break;
                case 9:
                    mViewPager.setPageTransformer(true, new RotateDownTransformer());
                    break;
                case 10:
                    mViewPager.setPageTransformer(true, new RotateUpTransformer());
                    break;
                case 11:
                    mViewPager.setPageTransformer(true, new ScaleInOutTransformer());
                    break;
                case 12:
                    mViewPager.setPageTransformer(true, new StackTransformer());
                    break;
                case 13:
                    mViewPager.setPageTransformer(true, new TabletTransformer());
                    break;
                case 14:
                    mViewPager.setPageTransformer(true, new ZoomInTransformer());
                    break;
                case 15:
                    mViewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
                    break;
                case 16:
                    mViewPager.setPageTransformer(true, new ZoomOutTranformer());
                    break;
                default:
                    break;
            }
       }
    }
 }
