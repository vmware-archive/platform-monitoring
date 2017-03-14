package io.pivotal.api.utils;

import javax.management.*;
import javax.management.modelmbean.DescriptorSupport;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

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