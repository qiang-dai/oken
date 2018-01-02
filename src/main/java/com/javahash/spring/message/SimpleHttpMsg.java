package com.javahash.spring.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * Created by DaiQiang on 2016-09-07.
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
////@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleHttpMsg {
    Integer errorCode;
    String errorMsg;
}

