package com.Bit_Builder.x_ray.app.enums;

public enum Status {
    PENDING,      // just uploaded, not analyzed yet
    ANALYZED,     // AI has analyzed it
    REVIEWED      // doctor has reviewed and added notes
}