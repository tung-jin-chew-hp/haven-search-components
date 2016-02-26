package com.hp.autonomy.searchcomponents.idol.category;

import lombok.Data;

/*
 * $Id:$
 *
 * Copyright (c) 2016, Autonomy Systems Ltd.
 *
 * Last modified by $Author$ on $Date$ 
 */
@Data
public class Cluster {
    private String title;
    private double score;
    private int x;
    private int y;
}
