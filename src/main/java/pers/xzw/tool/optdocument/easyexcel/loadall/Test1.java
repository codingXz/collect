package pers.xzw.tool.optdocument.easyexcel.loadall;

import com.alibaba.excel.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Test1 {

            // 模板
            /*"when (DW_RATE_BASE=11 and DW_RATE_CUR='HKD' and DW_RATE_PRD = 101) OR\n" +
                " (DW_RATE_BASE=11 and DW_RATE_CUR='CNY' and DW_RATE_PRD = 101) OR\n" +
                " (DW_RATE_BASE=11 and DW_RATE_CUR='USD' and DW_RATE_PRD = 101) OR\n" +
                " (DW_RATE_BASE=11 and DW_RATE_CUR='GBP' and DW_RATE_PRD = 101) OR\n" +
                " (DW_RATE_BASE=11 and DW_RATE_CUR='JPY' and DW_RATE_PRD = 101)  -- 省略n,excel 一行一行对应\n" +
                "then '001M'"*/

    public static void main(String[] args) {

        ExcelLoadHandler.readAndOptBusiness("D:\\test\\ncb-1.xlsx", (list) -> {
            String preStr = "when ";
            String or = " OR\n";
            String logicStr = "(DW_RATE_BASE=${rate} and DW_RATE_CUR='${cur}' and DW_RATE_PRD = ${prd})";
            String thenStr = "then '${thenData}' \n";

            List<ExcelLoadAllModel> list1 = (List<ExcelLoadAllModel>) list;
            List<TemplateObj> aList = new ArrayList<>();
            for (ExcelLoadAllModel loadAllModel : list1) {
                if (!StringUtils.isEmpty(loadAllModel.getColumn1()) &&
                        !StringUtils.isEmpty(loadAllModel.getColumn2()) &&
                        !StringUtils.isEmpty(loadAllModel.getColumn3()) &&
                        !StringUtils.isEmpty(loadAllModel.getColumn4())) {
                    aList.add(new TemplateObj(loadAllModel));
                }
                aList.add(new TemplateObj(loadAllModel));
            }

            // 根据thenData分组
            Map<String, List<TemplateObj>> groupCollect = aList.stream().filter(a -> !StringUtils.isEmpty(a.getThenData())).collect(Collectors.groupingBy(TemplateObj::getThenData));
            // 按thenData排序输出
            List<String> keySortCollect = groupCollect.keySet().stream().sorted().collect(Collectors.toList());
            StringBuilder stringBuilder = new StringBuilder();
            keySortCollect.forEach(k -> {
                List<TemplateObj> v = groupCollect.get(k);
                //开始拼接
                stringBuilder.append(preStr);
                for (int i = 0; i < v.size(); i++) {
                    TemplateObj obj = v.get(i);
                    stringBuilder.append(logicStr.replace("${rate}", obj.getRate()).replace("${cur}", obj.getCur()).replace("${prd}", obj.getPrd()));
                    // 最后一次遍历
                    if (i == v.size() - 1) {
                        stringBuilder.append("\n");
                    } else {
                        stringBuilder.append(or);
                    }
                }
                stringBuilder.append(thenStr.replace("${thenData}", k));
            });

            System.out.println("=======输出拼接结果========");
            System.out.println(stringBuilder.toString());
            return null;
        });

    }

    public static class TemplateObj {
        private String rate;
        private String cur;
        private String prd;
        private String thenData;

        public TemplateObj() {
        }

        public TemplateObj(String rate, String cur, String prd, String thenData) {
            this.rate = rate;
            this.cur = cur;
            this.prd = prd;
            this.thenData = thenData;
        }

        public TemplateObj(ExcelLoadAllModel model) {
            this.rate = model.getColumn1();
            this.cur = model.getColumn2();
            this.prd = model.getColumn3();
            this.thenData = model.getColumn4();
        }

        public String getRate() {
            return rate;
        }

        public void setRate(String rate) {
            this.rate = rate;
        }

        public String getCur() {
            return cur;
        }

        public void setCur(String cur) {
            this.cur = cur;
        }

        public String getPrd() {
            return prd;
        }

        public void setPrd(String prd) {
            this.prd = prd;
        }

        public String getThenData() {
            return thenData;
        }

        public void setThenData(String thenData) {
            this.thenData = thenData;
        }
    }
}
