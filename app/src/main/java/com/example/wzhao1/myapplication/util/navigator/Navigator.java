package com.example.wzhao1.myapplication.util.navigator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;

import com.example.wzhao1.myapplication.util.log.DLog;
import com.google.common.collect.Lists;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Class in charge of the navigation between fragments and activities.
 *
 */
public class Navigator {

    private static String MARKERS = "com.disney.wdpro.navigation.navigation.navigator.markers";
    private static String PENDING_NAVIGATIONS = "com.disney.wdpro.navigation.navigation.navigator.pending_navigations";

    private FragmentActivity activity;
    private FragmentManager fragmentManager;
    private int layoutId;
    private NavigationListener listener;

    /* State variables */
    private LinkedList<Integer> flowMarkers;
    private Bundle pendingNavigations;

    /**
     * Interface that listen for navigation events.
     */
    public static interface NavigationListener {
        /**
         * Event called before the navigator action is executed.
         *
         * @param navigator the instance of the navigator that will execute the navigation action.
         * @param entry navigator action to be executed.
         * @return if the navigator action was handled externally.
         */
        boolean onNavigate(Navigator navigator, NavigationEntry<?> entry);
    }

    /**
     * Construct a new navigator instance with {@link android.R.id#content} as the fragment container id.
     *
     * @param activity
     * @param savedInstanceState
     */
    public Navigator(FragmentActivity activity, Bundle savedInstanceState) {
        this(activity, savedInstanceState, android.R.id.content, null);
    }

    /**
     * Construct a new navigator instance.
     *
     * @param activity
     * @param savedInstanceState
     * @param containerId           The id of the container that will host fragments.
     * @throws java.lang.IllegalArgumentException if the provided layout id is invalid.
     */
    @SuppressWarnings("unchecked")
    public Navigator(FragmentActivity activity, Bundle savedInstanceState, int containerId, NavigationListener listener) {
        this.activity = activity;
        this.listener = listener;
        this.layoutId = containerId;
        this.fragmentManager = activity.getSupportFragmentManager();
        if (savedInstanceState == null || savedInstanceState.get(MARKERS) == null) {
            flowMarkers = Lists.newLinkedList();
        } else {
            flowMarkers = Lists.newLinkedList((List<Integer>) savedInstanceState.get(MARKERS));
        }
        restorePendingNavigations(savedInstanceState);
        fragmentManager.addOnBackStackChangedListener(new OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                //This is ugly, but we don't have a way to determine which was the entry that was removed!
                List<Integer> backStackEntries = Lists.newArrayList();
                for (int j = 0; j < fragmentManager.getBackStackEntryCount(); j++) {
                    BackStackEntry entry = fragmentManager.getBackStackEntryAt(j);
                    backStackEntries.add(entry.getId());
                }
                flowMarkers.retainAll(backStackEntries);
            }
        });
    }

    private void restorePendingNavigations(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(PENDING_NAVIGATIONS)) {
            pendingNavigations = savedInstanceState.getBundle(PENDING_NAVIGATIONS);
        } else {
            pendingNavigations = new Bundle();
        }
    }

    /**
     * Store the internal state of the navigator to be able to restore it in the future.
     *
     * @param outState
     */
    public void saveInstanceState(Bundle outState) {
        outState.putSerializable(MARKERS, flowMarkers);
        outState.putBundle(PENDING_NAVIGATIONS, pendingNavigations);
    }

    /**
     * Replace current container (layoutId) with specified fragment, and specified tag.
     *
     * @param fragment       fragment to show
     * @param tag            tag to associate with the fragment.
     * @param addToBackStack flag to add fragment in back stack.
     * @param containerId       Container id used for the fragment navigation
     * @param animation      fragment animation
     */
    private int pushFragment(int containerId, Fragment fragment, String tag, boolean addToBackStack,
            NavigationEntry.CustomAnimations animation) {
        String res = activity.getResources().getResourceName(containerId);
        if (res == null || !res.startsWith(activity.getPackageName())) {
            throw new IllegalArgumentException("Layout id provided to navigator is invalid.");
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (animation != null) {
            transaction.setCustomAnimations(animation.enter, animation.exit, animation.popEnter, animation.popExit);
        }
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        // use commitAllowingStateLoss per Support Library bug: https://code.google.com/p/android/issues/detail?id=19917
        // (TODO once we get rid of support lib we can remove this)
        return transaction.replace(containerId, fragment, tag).commitAllowingStateLoss();
    }

    /**
     * Enable navigation between fragments
     *
     * @param fragment - target of the navigation
     */
    public FragmentNavigationEntry.Builder to(Fragment fragment) {
        checkState(!fragment.isAdded(), "The fragment was already added to an activity before calling the navigator.");
        return new FragmentNavigationEntry.Builder(this, fragment);
    }

    /**
     * Enable navigation between activities
     *
     * @param intent - target of the navigation
     */
    public IntentNavigationEntry.Builder to(Intent intent) {
        return new IntentNavigationEntry.Builder(this, intent);
    }

    /**
     * Navigate between fragments
     *
     * @param entry
     */
    protected void navigateTo(FragmentNavigationEntry entry) {
        if (listener != null && listener.onNavigate(this, entry)) {
            return;
        }

        if (entry.isClearHistory()) {
            clearFragmentNavigationHistory();
        }

        Fragment target = entry.getTarget();
        String tag = entry.getTag();
        NavigationEntry.CustomAnimations animations = entry.getAnimations();
        Integer containerId = entry.getContainerId();
        if (containerId == null) {
            containerId = layoutId;
        }

        if (entry.isNoPush()) {
            pushFragment(containerId, target, tag, false, animations);
        } else {
            if (entry.isSubFlow()) {
                DLog.d("Starting subflow from fragment: %s", target.getClass().getSimpleName());
                checkState(flowMarkers.isEmpty(),
                        "You can only have one subflow. Nested subflows are not supported at the moment.");
                flowMarkers.add(pushFragment(containerId, target, null, true, animations));
            } else {
                pushFragment(containerId, target, tag, true, animations);
            }
        }
    }

    /**
     * Navigate between activities
     *
     * @param entry
     */
    protected void navigateTo(IntentNavigationEntry entry) {
        if (listener != null && listener.onNavigate(this, entry)) {
            return;
        }

        int requestCode = entry.getRequestCode();
        Intent target = entry.getTarget();
        if (requestCode == -1) {
            activity.startActivity(target);
        } else {
            int startFromFragmentId = entry.getStartFromFragmentId();
            if (startFromFragmentId == -1) {
                activity.startActivityForResult(target, requestCode);
            } else {
                Fragment fragment = fragmentManager.findFragmentById(startFromFragmentId);
                if (fragment == null) {
                    DLog.e("Trying to start an activity from an unexisting fragment with id: %s", startFromFragmentId);
                    return;
                } else {
                    activity.startActivityFromFragment(fragment, target, requestCode);
                }
            }
        }

        //Perform animation if any exist
        NavigationEntry.CustomAnimations animations = entry.getAnimations();
        if (animations != null) {
            activity.overridePendingTransition(animations.enter, animations.exit);
        }
    }

    /**
     * Clears the entire fragment navigation history.
     * This removes all the entries in the back stack.
     */
    public void clearFragmentNavigationHistory() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            BackStackEntry firstFragment = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(firstFragment.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            DLog.w("No backstack found trying to clear backstack.");
        }
    }

    /**
     * Finish the latest sub flow.
     * Pops back all the fragments until the one contained in the previous startShowFlow call.
     */
    public void finishSubFlow() {
        Integer entry = flowMarkers.pollLast();
        if (entry != null) {
            DLog.d("Ending subflow.");
            fragmentManager.popBackStack(entry, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     * Go in step back in the fragment navigation history.
     */
    public void navigateOneStepBack() {
        fragmentManager.popBackStack();
    }

    /**
     * Store a {@link NavigationEntry} as a pending navigation action, replacing
     * any existing value for the given navigationId.
     *
     * @param navigationId identifier associated with the navigation action
     * @param entry        pending navigation action
     */
    public void addPendingNavigation(String navigationId, NavigationEntry<?> entry) {
        pendingNavigations.putParcelable(navigationId, entry);
    }

    /**
     * Execute a pending navigation action associated with the given navigationId if any exist.
     *
     * @param navigationId identifier associated with the navigation action
     * @param forceRemove if true, force the pending @NavigationEntry to be removed after executed,
     *             even if it is sticky.
     *                    If false, force the pending @NavigationEntry to be kept.
     *                    If null, it is removed by default unless the @NavigationEntry is flagged as sticky.
     */
    public void executePendingNavigation(String navigationId, Boolean forceRemove) {
        NavigationEntry<?> parcelable = pendingNavigations.getParcelable(navigationId);
        boolean forcingRemove;

        // if forceRemove is not present, it is overrided by NavigationEntry sticky value
        if(forceRemove == null) {
            forcingRemove = parcelable != null && !parcelable.isSticky();
        } else {
            forcingRemove = forceRemove;
        }

        if (forcingRemove) {
            navigateTo(removePendingNavigation(navigationId));
        } else {
            navigateTo(parcelable);
        }
    }

    /**
     * Execute a pending navigation action associated with the given navigationId if any exist,
     * and remove the @NavigationEntry if it is no sticky.
     *
     * @param navigationId identifier associated with the navigation action
     */
    public void executePendingNavigation(String navigationId) {
        executePendingNavigation(navigationId, null);
    }

    /**
     * Removes a pending navigation action associated with the given navigationId.
     *
     * @param navigationId identifier associated with the navigation action
     * @return Returns the value that was stored under the navigationId, or null if there
     * was no such navigationId.
     */
    public NavigationEntry<?> removePendingNavigation(String navigationId) {
        NavigationEntry<?> parcelable = pendingNavigations.getParcelable(navigationId);
        pendingNavigations.remove(navigationId);
        return parcelable;
    }

    /**
     * Executes a navigation action
     *
     * @param entry navigation action to execute
     */
    public void navigateTo(NavigationEntry<?> entry) {
        if (entry instanceof FragmentNavigationEntry) {
            navigateTo((FragmentNavigationEntry) entry);
        } else if (entry instanceof IntentNavigationEntry) {
            navigateTo((IntentNavigationEntry) entry);
        }
    }
}
