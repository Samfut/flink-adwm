package adwater.predictor;

import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;
import org.jpmml.model.PMMLUtil;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DecisionTreePredictor {

    private Evaluator evaluator;

    private Evaluator loadPmml(){
        PMML pmml = new PMML();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/Users/yangs/Projects/adwater/TimeSeries/citybike/treemodel.pmml");
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

    public DecisionTreePredictor() {
        this.evaluator = this.loadPmml();
    }

    public double predict(int hour, int day, int dayofweek) {
        Map<String, Integer> data = new HashMap<String, Integer>();
        data.put("hour", hour);
        data.put("day", day);
        data.put("dayofweek", dayofweek);

        List<InputField> inputFields = this.evaluator.getInputFields();
        Map<FieldName, FieldValue> arguments = new LinkedHashMap<FieldName, FieldValue>();
        for (InputField inputField : inputFields) {
            FieldName inputFieldName = inputField.getName();
            Object rawValue = data.get(inputFieldName.getValue());
            FieldValue inputFieldValue = inputField.prepare(rawValue);
            arguments.put(inputFieldName, inputFieldValue);
        }
        Map<FieldName, ?> results = this.evaluator.evaluate(arguments);
        List<TargetField> targetFields = this.evaluator.getTargetFields();

        TargetField targetField = targetFields.get(0);
        FieldName targetFieldName = targetField.getName();

        Object targetFieldValue = results.get(targetFieldName);

        double primitiveValue = 0;
        if(targetFieldValue instanceof Computable) {
            Computable computable = (Computable) targetFieldValue;
            primitiveValue = (Double)computable.getResult();
        }

        return primitiveValue;
    }

    public static void main(String[] args) {
        DecisionTreePredictor d = new DecisionTreePredictor();
        System.out.println(d.predict(0,1,0));
        System.out.println(d.predict(1,1,0));
        System.out.println(d.predict(2,1,0));
        System.out.println(d.predict(3,1,0));
        System.out.println(d.predict(4,1,0));
        System.out.println(d.predict(5,1,0));
        System.out.println(d.predict(6,1,0));
    }
}
