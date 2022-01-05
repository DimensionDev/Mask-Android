package com.dimension.maskbook.manifest;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Manifest {

    @SerializedName("external-uri")
    List<String> externalWebsite;

    @SerializedName("universal-link")
    List<String> universalLink;
}
