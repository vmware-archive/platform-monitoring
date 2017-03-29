package io.pivotal.platformMonitoring.kpiValidator.utils;


import javax.management.*;
import javax.management.modelmbean.DescriptorSupport;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class DynamicMapMBean implements DynamicMBean {
    private String className;
    private Map<String, Double> values;
    private String description;
    private Map<String, String> tags;
    private MBeanInfo mbi;
    private final Long timestamp;

    public DynamicMapMBean(String name) {
        Map<String, String> tags = new HashMap<>();
        tags.put("deployment", UUID.randomUUID().toString());
        tags.put("job", "some-job");
        tags.put("index", UUID.randomUUID().toString());
        tags.put("ip", "0.0.0.0");

        this.values = new HashMap<>();
        this.tags = tags;
        this.description = name;
        this.className = "java.lang.Double";
        this.timestamp = System.currentTimeMillis();

        buildMBeanInfo();
    }

    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return values.get(attribute);
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        setAttribute(attribute.getName());
    }

    public void setAttribute(String attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        values.put(attribute, 0.0d);
        buildMBeanInfo();
    }

    public AttributeList getAttributes(String[] attributes) {
        return new AttributeList(Arrays.stream(attributes)
            .map(s -> new Attribute(s, values.get(s)))
            .collect(toList()));
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
        Descriptor descriptor = new DescriptorSupport();

        List<MBeanAttributeInfo> attrs = values.keySet().stream()
            .map(name -> new MBeanAttributeInfo(name, "java.lang.Double", name, true, false, false))
            .collect(toList());

        List<MBeanOperationInfo> ops = values.keySet().stream()
            .map(name -> new MBeanOperationInfo(name, name, null, "java.lang.Double", MBeanOperationInfo.INFO))
            .collect(toList());

        values.forEach(descriptor::setField);
        tags.forEach(descriptor::setField);

        MBeanAttributeInfo[] attrsArray = attrs.toArray(new MBeanAttributeInfo[attrs.size()]);
        MBeanOperationInfo[] opsArray = ops.toArray(new MBeanOperationInfo[ops.size()]);

        mbi = new MBeanInfo(className, description, attrsArray, null, opsArray, null, descriptor);
    }
}
