package vn.nip.around.Bean;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Class.GlobalClass;
import vn.nip.around.R;

/**
 * Created by viminh on 11/25/2016.
 */

public class BeanOrder {
    private int id;
    private String create_date;
    private int status;
    private int total;
    private int[] order_type;
    private String order_code;
    private String type;
    private String address;
    private String recipent_name;

    private List<BeanPoint> locations;

    public static boolean isGift(BeanOrder bean) {
        for (int i = 0; i < bean.getOrder_type().length; i++) {
            if (bean.getOrder_type()[i] == 0) {
                return true;
            }
        }
        return false;
    }

    public static BeanOrder getValueByKey(int id, List<BeanOrder> list) {
        for (BeanOrder bean : list) {
            if (bean.getId() == id) {
                return bean;
            }
        }
        return null;
    }

    public static String getStringType(BeanOrder beanOrder) {
        String retValue = StringUtils.EMPTY;
        List<Integer> repairOrderType = new ArrayList();
        for (int orderType : beanOrder.getOrder_type()) {
            if (repairOrderType.contains(orderType)) {
                continue;
            }
            repairOrderType.add(orderType);
            switch (orderType) {
                //GIFTING
                case 0:
                    retValue += GlobalClass.getActivity().getString(R.string.shopping_gifting) + " - ";
                    break;
                //TRANSPORT
                case 1:
                    retValue += GlobalClass.getActivity().getString(R.string.transport) + " - ";
                    break;
                //PURCHASE
                case 2:
                    retValue += GlobalClass.getActivity().getString(R.string.purchase) + " - ";
                    break;
                //COD
                case 3:
                    retValue += GlobalClass.getActivity().getString(R.string.collect) + " - ";
                    break;
            }
        }
        if (retValue.length() > 3) {
            retValue = retValue.substring(0, retValue.length() - 3);
        }
        return retValue;
    }

//    public static String getStringType(BeanOrder bean, int reOrder) {
//        String retValue = "";
//        if (reOrder == 1) {
//            for (int i = 0; i < bean.getOrder_type().length; i++) {
//                retValue = "";
//                switch (bean.getOrder_type()[i]) {
//                    case 0:
//                        if (retValue.equals(GlobalClass.getActivity().getString(R.string.gifting))) {
//                            retValue = GlobalClass.getActivity().getString(R.string.gifting);
//                        }
//                        retValue += " - " + GlobalClass.getActivity().getString(R.string.gifting);
//                        break;
//                    case 1:
//                        if (retValue.equals(GlobalClass.getActivity().getString(R.string.transport))) {
//                            retValue = GlobalClass.getActivity().getString(R.string.transport);
//                        }
//                        retValue += " - " + GlobalClass.getActivity().getString(R.string.transport);
//                        break;
//                    case 2:
//                        if (retValue.equals(GlobalClass.getActivity().getString(R.string.purchase))) {
//                            retValue = GlobalClass.getActivity().getString(R.string.purchase);
//                        }
//                        retValue += " - " + GlobalClass.getActivity().getString(R.string.purchase);
//                        break;
//                    case 3:
//                        if (retValue.equals(GlobalClass.getActivity().getString(R.string.collect))) {
//                            retValue = GlobalClass.getActivity().getString(R.string.collect);
//                        }
//                        retValue += " - " + GlobalClass.getActivity().getString(R.string.collect);
//                        break;
//                }
//            }
//        }
//        return retValue.substring(2, retValue.length()).trim();
//    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public int[] getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int[] order_type) {
        this.order_type = order_type;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BeanPoint> getLocations() {
        return locations;
    }

    public void setLocations(List<BeanPoint> locations) {
        this.locations = locations;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRecipent_name() {
        return recipent_name;
    }

    public void setRecipent_name(String recipent_name) {
        this.recipent_name = recipent_name;
    }
}
