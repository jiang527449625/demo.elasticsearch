package com.demo.elasticsearch.Model;

import com.demo.elasticsearch.Model.ResultCode;
import lombok.Data;

@Data
public class Head {
        private String msg;
        private Integer code;
        private String success;

        public Head(){

        }

        public Head(String msg, ResultCode code, String success) {
            this.msg = msg;
            this.code = code.code;
            this.success = success;
        }

        public Head(String msg, Integer code, String success) {
            this.msg = msg;
            this.code = code;
            this.success = success;
        }

}
