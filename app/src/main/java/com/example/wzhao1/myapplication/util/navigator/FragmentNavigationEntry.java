package com.example.wzhao1.myapplication.util.navigator;

import android.os.Parcel;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.wzhao1.myapplication.util.log.DLog;

/**
 * Fragment navigation transaction.
 */
public class FragmentNavigationEntry extends NavigationEntry<Fragment> {
    private final String tag;
    private final boolean subFlow;
    private boolean clearHistory;
    private Integer containerId;

    /**
     * Tells the if the fragment is going to be pushed to the backstack.
     */
    private final boolean noPush;

    FragmentNavigationEntry(Builder builder) {
        super(builder);
        this.tag = builder.tag;
        this.subFlow = builder.subFlow;
        this.noPush = builder.noPush;
        this.clearHistory = builder.clearHistory;
        this.containerId = builder.containerId;
    }

    public String getTag() {
        return tag == null ? getTarget().getClass().getSimpleName() : tag;
    }

    /**
     * Indicates if the fragments starts a sub flow
     * @return
     */
    public boolean isSubFlow() {
        return subFlow;
    }

    /**
     * Calls {@link Navigator#clearFragmentNavigationHistory()} before navigating.
     * This will remove all the entries in the back stack before the navigation action is executed.
     * <p/>
     * By default is false.
     * @return if the history is going to be cleared
     */
    public boolean isClearHistory() {
        return clearHistory;
    }

    /**
     * Tells if the target fragment is going to be pushed to the backstack.
     */
    public boolean isNoPush() {
        return noPush;
    }

    /**
     * Gets the container id where the navigation is going to take place.
     * @return the id of the navigation container or null if non was set.
     */
    public Integer getContainerId() {
        return containerId;
    }

    @Override
    protected Class<?> getInternalTargetClass() throws ClassNotFoundException {
        return getTarget().getClass();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        storeFragment(getTarget(), dest);
        super.writeToParcel(dest, flags);
        dest.writeValue(tag);
        dest.writeValue(subFlow);
        dest.writeValue(noPush);
        dest.writeValue(clearHistory);
    }

    private void storeFragment(Fragment fragment, Parcel dest) {
        dest.writeValue(fragment.getClass());
        dest.writeValue(fragment.getArguments());
    }

    public static final Creator<FragmentNavigationEntry> CREATOR
            = new Creator<FragmentNavigationEntry>() {
        @Override
        public FragmentNavigationEntry createFromParcel(Parcel parcelIn) {
            return new FragmentNavigationEntry(parcelIn);
        }
        @Override
        public FragmentNavigationEntry[] newArray(int size) {
            return new FragmentNavigationEntry[size];
        }
    };

    FragmentNavigationEntry(Parcel parcelIn) {
        super(parcelIn, restoreFragment(parcelIn));
        tag = (String) parcelIn.readValue(null);
        subFlow = (boolean) parcelIn.readValue(null);
        noPush = (boolean) parcelIn.readValue(null);
        clearHistory = (boolean) parcelIn.readValue(null);
    }

    /**
     * Builder to create a Fragment Navigation entry.
     */
    public static class Builder extends NavigationEntry.Builder<Builder, Fragment> {
        private String tag;
        private boolean subFlow;
        private boolean noPush;
        private boolean clearHistory;
        private Integer containerId;

        /**
         * Create a Builder without a Navigator associated to it, the method {@link #navigate()} can't be use
         * if an instance of the Builder is created using this constructor.
         * @param target
         */
        public Builder(Fragment target) {
            this(null, target);
        }

        /**
         * This constructor is intended to be used inside the navigator from {@link Navigator#to(Fragment)} and should be package access,
         * keeping it public for backward compatibility. Use {@link Builder#Builder(Fragment)} instead.
         * @param navigator
         * @param target
         * @deprecated the scope of this constructor will be reduced to package private in the next version.
         */
        @Deprecated
        public Builder(Navigator navigator, Fragment target) {
            super(navigator, target);
        }

        /**
         * Adds an optional tag to the fragment.
         * By default is null.
         */
        public Builder withTag(String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * Tells if the target fragment is not going to be pushed to the backstack.<br>
         * If it isn't pushed the current content of the fragment will be replaced with the new fragment.<br>
         * This is intended to be used when there are no entries in the backstack
         * (since this will introduce unexpected behaviors when interacting with the same view container).
         * Info: http://stackoverflow.com/questions/12529499/problems-with-android-fragment-back-stack
         * <br>
         * By default the fragment will be pushed.
         */
        public Builder noPush() {
            this.noPush = true;
            return this;
        }

        /**
         * Calls {@link Navigator#clearFragmentNavigationHistory()} before navigating.
         * This will remove all the entries in the back stack before the navigation action is executed.
         * <p/>
         * By default is false
         */
        public Builder clearHistory() {
            this.clearHistory = true;
            return this;
        }

        /**
         * Sets the container id where the navigation is going to take place.
         */
        public Builder withContainerId(int containerId) {
            this.containerId = containerId;
            return this;
        }

        /**
         * Builds the {@link FragmentNavigationEntry}
         *
         * @return the navigation event.
         */
        @Override
        public FragmentNavigationEntry build() {
            return new FragmentNavigationEntry(this);
        }

        /**
         * Starts a navigation subflow inside an activity
         */
        public void startSubFlow() {
            subFlow = true;
            navigate();
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private static Fragment restoreFragment(Parcel parcelIn) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) ((Class) parcelIn.readValue(null)).newInstance();
            fragment.setArguments((android.os.Bundle) parcelIn.readValue(null));
        } catch (InstantiationException e) {
            DLog.e(e, "Error restoring fragment");
        } catch (IllegalAccessException e) {
            DLog.e(e, "Error restoring fragment");
        }
        return fragment;
    }

}
