package com.example.wzhao1.myapplication.util.navigator;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Parcel;
import android.support.v4.app.Fragment;

/**
 * Intent navigation transaction.
 */
public class IntentNavigationEntry extends NavigationEntry<Intent> {
    private int requestCode = -1;
    private int originatorFragmentId = -1;

    IntentNavigationEntry(Builder builder) {
        super(builder);
        this.requestCode = builder.code;
        this.originatorFragmentId = builder.originatorFragmentId;
    }

    IntentNavigationEntry(Parcel parcelIn) {
        super(parcelIn, (Intent) parcelIn.readValue(null));
        requestCode = parcelIn.readInt();
        originatorFragmentId = parcelIn.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(getTarget());
        super.writeToParcel(dest, flags);
        dest.writeInt(requestCode);
        dest.writeInt(originatorFragmentId);
    }

    public static final Creator<IntentNavigationEntry> CREATOR
            = new Creator<IntentNavigationEntry>() {
        @Override
        public IntentNavigationEntry createFromParcel(Parcel parcelIn) {
            return new IntentNavigationEntry(parcelIn);
        }

        @Override
        public IntentNavigationEntry[] newArray(int size) {
            return new IntentNavigationEntry[size];
        }
    };

    /**
     * @return the request code with a default value of -1
     */
    public int getRequestCode() {
        return requestCode;
    }

    /**
     * @return the originator fragment id.
     */
    public int getStartFromFragmentId() {
        return originatorFragmentId;
    }

    @Override
    protected Class<?> getInternalTargetClass() throws ClassNotFoundException {
        ComponentName component = getTarget().getComponent();
        if (component != null) {
            return Class.forName(component.getClassName());
        }
        return null;
    }

    /**
     * Builder to create an Activity Navigation entry.
     */
    public static class Builder extends NavigationEntry.Builder<Builder, Intent> {

        private int code = -1;
        private int originatorFragmentId = -1;

        /**
         * Create a Builder without a Navigator associated to it, the method {@link #navigate()} can't be use
         * if an instance of the Builder is created using this constructor.
         * @param target
         */
        public Builder(Intent target) {
            this(null, target);
        }

        /**
         * This constructor is intended to be used inside the navigator from {@link Navigator#to(Intent)} and should be package access,
         * keeping it public for backward compatibility. Use {@link Builder#Builder(Intent)} instead.
         * @param navigator
         * @param target
         * @deprecated the scope of this constructor will be reduced to package private in the next version.
         */
        @Deprecated
        public Builder(Navigator navigator, Intent target) {
            super(navigator, target);
        }

        /**
         * TODO change this to be more self explanatory...
         * something like forResultIn(int code) or forResultIn(Fragment origin, int code)
         */
        public Builder withRequestCode(int code) {
            this.code = code;
            return this;
        }

        public Builder withFlags(int flags) {
            this.target.setFlags(flags);
            return this;
        }

        /**
         * If set, and the activity being launched is already running in the current task, then instead of launching a new instance of that activity,
         * all of the other activities on top of it will be closed and this Intent will be delivered to the (now on top) old activity as a new Intent.<br>
         * <br>
         * See {@link android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP} for more information.
         */
        public Builder clearTop() {
            return withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        /**
         * If set, this flag will cause any existing task that would be associated with the activity to be cleared before the activity is started.
         * That is, the activity becomes the new root of an otherwise empty task, and any old activities are finished.
         * This can only be used in conjunction with FLAG_ACTIVITY_NEW_TASK.<br>
         * <br>
         * See {@link android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK} and {@link android.content.Intent.FLAG_ACTIVITY_NEW_TASK} for more information.
         */
        public Builder clearStack() {
            return withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        /**
         * Sets the originator fragment, this will call {@link android.app.Activity#startActivityFromFragment(android.app.Fragment, android.content.Intent, int)}.<br>
         * <p/>
         * Modifies the standard behavior to allow results to be delivered to fragments.
         * This imposes a restriction that requestCode be <= 0xffff.
         */
        public Builder startFromFragment(Fragment origin) {
            originatorFragmentId = origin.getId();
            return this;
        }

        /**
         * Builds the {@link IntentNavigationEntry}
         *
         * @return the navigation event.
         */
        @Override
        public IntentNavigationEntry build() {
            return new IntentNavigationEntry(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

    }
}
