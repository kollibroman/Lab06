package org.filip.ui.viewModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TankerViewModel
{
    private int tankerId;
    private String name;
    private boolean readyToServe;

    public TankerViewModel(int tankerId, String name, boolean readyToServe) {
        this.tankerId = tankerId;
        this.name = name;
        this.readyToServe = readyToServe;
    }
}

