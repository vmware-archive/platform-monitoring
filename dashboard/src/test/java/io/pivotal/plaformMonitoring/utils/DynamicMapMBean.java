package io.pivotal.plaformMonitoring.utils;

import javax.management.*;
import javax.management.modelmbean.DescriptorSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicMapMBean implements DynamicMBean {

    private String className;
    private Map<String, Double> values;
    private String description;
    private Map<String, String> tags;
    private MBeanInfo mbi;

    public DynamicMapMBean(DataPoint dataPoint, JMXNamingService namingService) {
        this.values = new HashMap<>();
        this.tags = dataPoint.getTags();
        this.description = namingService.getName(dataPoint);
        this.className = namingService.getName(dataPoint);

        buildMBeanInfo();
    }

    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return values.get(attribute);
    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        values.put(attribute.getName(), (Double) attribute.getValue());
        buildMBeanInfo();
    }

    public AttributeList getAttributes(String[] attributes) {
        List<Attribute> list = new ArrayList<>();
        for (String s : attributes) {
            list.add(new Attribute(s, values.get(s)));
        }
        return new AttributeList(list);
    }

    public AttributeList setAttributes(AttributeList attributes) {
        return null;
    }

    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        return values.get(actionName);
    }

    public MBeanInfo getMBeanInfo() {
        return mbi;
    }

    private void buildMBeanInfo() {
        List<MBeanAttributeInfo> attrs = new ArrayList<>();
        List<MBeanOperationInfo> ops = new ArrayList<>();

        Descriptor descriptor = new DescriptorSupport();

        for (String name : values.keySet()) {
            Number val = values.get(name);
            descriptor.setField(name, val);

            attrs.add(new MBeanAttributeInfo(name, "java.lang.Double", name, true, false, false));
            ops.add(new MBeanOperationInfo(name, name, null, "java.lang.Double", MBeanOperationInfo.INFO));
        }


        for (String name : tags.keySet()) {
            String val = tags.get(name);
            descriptor.setField(name, val);
        }

        MBeanAttributeInfo[] attrsArray = new MBeanAttributeInfo[attrs.size()];
        for (int i = 0; i < attrs.size(); ++i) {
            attrsArray[i] = attrs.get(i);
        }

        MBeanOperationInfo[] opsArray = new MBeanOperationInfo[ops.size()];
        for (int i = 0; i < ops.size(); ++i) {
            opsArray[i] = ops.get(i);
        }

        mbi = new MBeanInfo(className, description,
                attrsArray,
                null,
                opsArray,
                null,
                descriptor);
    }
}