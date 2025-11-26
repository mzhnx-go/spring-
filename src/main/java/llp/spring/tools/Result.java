package llp.spring.tools;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Result {
    private boolean success = true;
    private String msg = "";
    private Map<String,Object> map=new HashMap();
    public Result(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    public void setErrorMessage(String msg){
        this.success = false;
        this.msg = msg;
    }

}
