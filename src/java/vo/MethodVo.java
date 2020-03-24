package vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author linxixin@cvte.com
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MethodVo {
    private String packageName;
    private String className;
    private String methodName;
    private List<String> paramNames;
}
