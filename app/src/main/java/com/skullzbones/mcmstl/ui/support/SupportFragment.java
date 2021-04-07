package com.skullzbones.mcmstl.ui.support;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.skullzbones.mcmstl.R;

import org.sufficientlysecure.donations.BuildConfig;
import org.sufficientlysecure.donations.DonationsFragment;

public class SupportFragment extends Fragment {
    private static final String GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjzKG52Dg8HKuCNTUS3OW2A63ngtf8atqUN10+IarL/vzmRgK9rTcVyPPeZ6mlFpklu6R8+S78a1qPnDv3CoFyO+iU3zOU1PsnYPmwtxGiadIlAeSdUkn4p7ndbVWBC5DJ7rbhLAwQl+q6Dpy/PUw/75B7XD147F2BznCxoj7hhnEtsezCw5rebQb4iDnmyMLMy6O4FD77HPHLNkNYt0UAgSzi25F0ZAlIXAtbMucbFm02akZT6nhkwMMkC0U3hBRQPJj5+WZyrlSX7ywX7GXEx3JKfRWTPyBx1xu6dOexs6kypT0+F4aP24il2oBCmWdGi3pF7J/j4oYTwLE351kIQIDAQAB";
    private static final String[] GOOGLE_CATALOG = new String[]{"mcmstl_donation_1",
            "mcmstl_donation_2", "mcmstl_donation_3",};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        DonationsFragment donationsFragment;
        donationsFragment = DonationsFragment.newInstance(BuildConfig.DEBUG, true, GOOGLE_PUBKEY, GOOGLE_CATALOG,
                getResources().getStringArray(R.array.donation_google_catalog_values), false, null, null,
                null, false, null, null, false, null);
        ft.replace(R.id.donations_activity_container, donationsFragment, "donationsFragment");
        ft.commit();

        return inflater.inflate(R.layout.support_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("donationsFragment");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}