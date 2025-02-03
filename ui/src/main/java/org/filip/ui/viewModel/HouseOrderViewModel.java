package org.filip.ui.viewModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HouseOrderViewModel
{
    private String houseName;
    private String orderTime;
    private String status;

    public HouseOrderViewModel(String houseName, String orderTime, String status) {
        this.houseName = houseName;
        this.orderTime = orderTime;
        this.status = status;
    }
}
