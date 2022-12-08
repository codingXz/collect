package pers.xzw.tool.optdocument.easyexcel.loadall;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import pers.xzw.tool.optdocument.BusinessHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * easyExcel全量一次性加载指定文件到内存，并进行业务处理
 */
public class ExcelLoadHandler {

    /** 调用示例 */
    public static void main(String[] args) {
        readAndOptBusiness("D:\\test\\conf.xlsx", (list) -> {
            System.out.println(list);
            return null;
        });
    }

    /**
     * 说明：
     * 默认读取sheet第一页，从第二行开始取数
     */
    public static void readAndOptBusiness(String readPath, BusinessHandler businessHandler) {
        try {
            // sheetNo --> 读取哪个 表单
            // headLineMun --> 从哪一行开始读取( 不包括定义的这一行，好比 headLineMun为2 ，那么取出来的数据是从 第三行的数据开始读取 )
            // clazz --> 将读取的数据，转化成对应的实体，须要 extends BaseRowModel
            Sheet sheet = new Sheet(1, 1, ExcelLoadAllModel.class);

            // 这里 取出来的是 ExcelModel实体 的集合
            List<Object> readList = EasyExcelFactory.read(new FileInputStream(readPath), sheet);
            // 存 ExcelModel 实体的 集合
            List<ExcelLoadAllModel> list = new ArrayList<ExcelLoadAllModel>();
            for (Object obj : readList) {
                list.add((ExcelLoadAllModel) obj);
            }
            businessHandler.call(list);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
