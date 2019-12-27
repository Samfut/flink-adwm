package adwater.predictor;

import org.dmg.pmml.PMML;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.ModelEvaluatorFactory;
import org.jpmml.model.PMMLUtil;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class XGboostPredictor {
    private Evaluator loadPmml(){
        PMML pmml = new PMML();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/Users/yangs/Desktop/result/xgmodel.pmml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStream is = inputStream;
        try {
            pmml = PMMLUtil.unmarshal(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
        Evaluator evaluator = modelEvaluatorFactory.newModelEvaluator(pmml);
        pmml = null;
        return evaluator;
    }
}
