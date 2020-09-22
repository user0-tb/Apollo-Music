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

package com.andrew.apollo.widgets.theme;

import android.content.Context;
import android.util.AttributeSet;

import com.andrew.apollo.R;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * This is a custom {@link TitlePageIndicator} that is made themeable by
 * allowing developers to choose the background and the selected and unselected
 * text colors.
 *
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class ThemeableTitlePageIndicator extends TitlePageIndicator {

    /**
     * @param context The {@link Context} to use
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public ThemeableTitlePageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Theme the background
        setBackgroundResource(R.drawable.tpi_background);/*
        // Theme the selected text color
        setSelectedColor(ResourcesCompat.getColor(context.getResources(), R.color.tpi_selected_text_color, null));
        // Theme the unselected text color
        setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.tpi_unselected_text_color, null));
        // Theme the footer
        setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.tpi_footer_color, null));*/
    }
}