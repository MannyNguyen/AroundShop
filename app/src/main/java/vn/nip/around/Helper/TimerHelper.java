package vn.nip.around.Helper;

import android.os.Handler;

import vn.nip.around.Class.GlobalClass;

/**
 * Created by viminh on 12/12/2016.
 */

public class TimerHelper {
    public static int pingTime;
    public static int requestShipperTimeout;
    public static int responseShipperTimeout;

    private static Handler handlerRequest;
    private static Runnable runnableRequest;
    private static Handler handlerResponse;
    private static Runnable runnableResponse;

    //Thực hiện nếu không nhận được phản hồi từ server khi Request shipper
    public static void excuteHandlerRequestShipper() {
//        GlobalClass.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                handlerRequest = new Handler();
//                runnableRequest = new Runnable() {
//                    @Override
//                    public void run() {
//                        Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
//                        if (fragment instanceof MatchFragment) {
//                            CustomDialog.showMessage(GlobalClass.getActivity(), GlobalClass.getActivity().getResources().getString(R.string.timeout), GlobalClass.getActivity().getResources().getString(R.string.cannot_shipper), new ICallback() {
//                                @Override
//                                public void excute() {
//                                    GlobalClass.FragmentHelper.pop(getActivity());
//                                }
//                            });
//                        }
//                        cancelHandlerRequestShipper();
//                    }
//                };
//                handlerRequest.postDelayed(runnableRequest, TimerHelper.responseShipperTimeout * 1000);
//            }
//        });

    }

    public static void cancelHandlerRequestShipper() {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (handlerRequest != null) {
                    handlerRequest.removeCallbacks(runnableRequest);
                }
            }
        });

    }

    public static void excuteHandlerResponseShipper() {
//        GlobalClass.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                handlerResponse = new Handler();
//                runnableResponse = new Runnable() {
//                    @Override
//                    public void run() {
//                        CustomDialog.showMessage(GlobalClass.getActivity(), GlobalClass.getActivity().getResources().getString(R.string.timeout), GlobalClass.getActivity().getResources().getString(R.string.cannot_shipper), new ICallback() {
//                            @Override
//                            public void excute() {
//                                GlobalClass.FragmentHelper.pop(getActivity());
//                            }
//                        });
//                        cancelHandlerResponseShipper();
//                    }
//
//                };
//                handlerResponse.postDelayed(runnableResponse, TimerHelper.responseShipperTimeout * 1000);
//            }
//        });

    }

    public static void cancelHandlerResponseShipper() {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (handlerResponse != null) {
                    handlerResponse.removeCallbacks(runnableResponse);
                }
            }
        });

    }
}
