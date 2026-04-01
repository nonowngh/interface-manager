package com.imc.interfacemanager.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InterfaceType {
    REALTIME("실시간"),
    BATCH("배치");

    private final String description;
}
