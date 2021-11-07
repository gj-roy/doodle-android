/*
 * This file is part of Grocy Android.
 *
 * Grocy Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grocy Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Grocy Android. If not, see http://www.gnu.org/licenses/.
 *
 * Copyright (c) 2020-2021 by Patrick Zedler and Dominic Zedler
 */

package xyz.zedler.patrick.doodle.fragment.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.Locale;
import java.util.Objects;
import xyz.zedler.patrick.doodle.Constants;
import xyz.zedler.patrick.doodle.R;
import xyz.zedler.patrick.doodle.activity.MainActivity;
import xyz.zedler.patrick.doodle.adapter.LanguageAdapter;
import xyz.zedler.patrick.doodle.databinding.FragmentBottomsheetListSelectionBinding;
import xyz.zedler.patrick.doodle.fragment.OtherFragment;
import xyz.zedler.patrick.doodle.model.Language;
import xyz.zedler.patrick.doodle.util.LocaleUtil;
import xyz.zedler.patrick.doodle.util.PrefsUtil;
import xyz.zedler.patrick.doodle.util.SystemUiUtil;

public class LanguagesBottomSheetDialogFragment extends BaseBottomSheetDialogFragment
    implements LanguageAdapter.LanguageAdapterListener {

  private final static String TAG = "LanguagesBottomSheet";

  private FragmentBottomsheetListSelectionBinding binding;
  private MainActivity activity;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    binding = FragmentBottomsheetListSelectionBinding.inflate(
        inflater, container, false
    );

    activity = (MainActivity) requireActivity();
    String selectedCode = getSharedPrefs().getString(
        Constants.PREF.LANGUAGE, Constants.DEF.LANGUAGE
    );

    binding.textListSelectionTitle.setText(getString(R.string.action_language_select));
    binding.textListSelectionDescription.setText(getString(R.string.other_language_description));
    binding.textListSelectionDescription.setVisibility(View.VISIBLE);

    binding.recyclerListSelection.setLayoutManager(
        new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    );
    binding.recyclerListSelection.setAdapter(
        new LanguageAdapter(LocaleUtil.getLanguages(activity), selectedCode, this)
    );

    return binding.getRoot();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  @Override
  public void onItemRowClicked(Language language) {
    String previousCode = getSharedPrefsBasic().getString(Constants.PREF.LANGUAGE, null);
    String selectedCode = language != null ? language.getCode() : null;

    if (Objects.equals(previousCode, selectedCode)) {
      return;
    } else if (previousCode == null || selectedCode == null) {
      Locale localeDevice = LocaleUtil.getNearestSupportedLocale(
          activity, LocaleUtil.getDeviceLocale()
      );
      String codeToCompare = previousCode == null ? selectedCode : previousCode;
      if (Objects.equals(localeDevice.toString(), codeToCompare)) {
        OtherFragment fragment = (OtherFragment) activity.getCurrentFragment();
        fragment.setLanguage(language);
        dismiss();
      } else {
        new Handler().postDelayed(() -> PrefsUtil.restartToApply(activity), 100);
      }
    } else {
      new Handler().postDelayed(() -> PrefsUtil.restartToApply(activity), 100);
    }

    getSharedPrefsBasic().edit().putString(Constants.PREF.LANGUAGE, selectedCode).apply();
  }

  @Override
  public void applyBottomInset(int bottom) {
    binding.recyclerListSelection.setPadding(
        0, SystemUiUtil.dpToPx(requireContext(), 8),
        0, SystemUiUtil.dpToPx(requireContext(), 8) + bottom
    );
  }

  @NonNull
  @Override
  public String toString() {
    return TAG;
  }
}