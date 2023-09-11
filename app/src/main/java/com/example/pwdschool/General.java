package com.example.pwdschool;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class General {
    public static void replaceFragment(
            FragmentManager fragmentManager,
            int containerId,
            Fragment fragment,
            boolean addToBackStack,
            String fragmentTag,
            int enterAnim,
            int exitAnim,
            int popEnterAnim,
            int popExitAnim
    ) {
        if (fragmentManager != null && fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Set custom animations if needed
            transaction.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);

            transaction.replace(containerId, fragment, fragmentTag);

            if (addToBackStack) {
                transaction.addToBackStack(fragmentTag);
            }

            transaction.commit();
        }
    }


}
