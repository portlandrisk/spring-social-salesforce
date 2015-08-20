package org.springframework.social.salesforce.api.impl;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Copy pasted from MappingJacksonHttpMessageConverter that accepts application/octetstream media types and handles it as json.
 * InvoiceIt is sending responses with the wrong media type.
 *
 */

public class InvoiceItHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private ObjectMapper objectMapper = new ObjectMapper();

    private boolean prefixJson = false;

    /**
     * Construct a new {@code BindingJacksonHttpMessageConverter}.
     */
    public InvoiceItHttpMessageConverter() {
//        super(new MediaType("application", "json", DEFAULT_CHARSET));
        super(new MediaType("application","octetstream",DEFAULT_CHARSET));

    }

    /**
     * Sets the {@code ObjectMapper} for this view. If not set, a default
     * {@link ObjectMapper#ObjectMapper() ObjectMapper} is used.
     * <p>Setting a custom-configured {@code ObjectMapper} is one way to take further control of the JSON serialization
     * process. For example, an extended {@link com.fasterxml.jackson.databind.ser.SerializerFactory} can be configured that provides
     * custom serializers for specific types. The other option for refining the serialization process is to use Jackson's
     * provided annotations on the types to be serialized, in which case a custom-configured ObjectMapper is unnecessary.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "'objectMapper' must not be null");
        this.objectMapper = objectMapper;
    }

    /**
     * Indicates whether the JSON output by this view should be prefixed with "{} &&". Default is false.
     * <p> Prefixing the JSON string in this manner is used to help prevent JSON Hijacking. The prefix renders the string
     * syntactically invalid as a script so that it cannot be hijacked. This prefix does not affect the evaluation of JSON,
     * but if JSON validation is performed on the string, the prefix would need to be ignored.
     */
    public void setPrefixJson(boolean prefixJson) {
        this.prefixJson = prefixJson;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        JavaType javaType = getJavaType(clazz);
        return this.objectMapper.canDeserialize(javaType) && canRead(mediaType);
    }

    /**
     * Returns the Jackson {@link JavaType} for the specific class.
     * <p/>
     * <p>Default implementation returns {@link TypeFactory#uncheckedSimpleType(Class)}}, but this can be overridden
     * in subclasses, to allow for custom generic collection handling. For instance:
     * <pre class="code">
     * protected JavaType getJavaType(Class&lt;?&gt; clazz) {
     * if (List.class.isAssignableFrom(clazz)) {
     * return TypeFactory.collectionType(ArrayList.class, MyBean.class);
     * } else {
     * return super.getJavaType(clazz);
     * }
     * }
     * </pre>
     *
     * @param clazz the class to return the java type for
     * @return the java type
     */
    protected JavaType getJavaType(Class<?> clazz) {
        return TypeFactory.defaultInstance().uncheckedSimpleType(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return this.objectMapper.canSerialize(clazz) && canWrite(mediaType);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // should not be called, since we override canRead/Write instead
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        JavaType javaType = getJavaType(clazz);
        try {
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        } catch (JsonParseException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        JsonEncoding encoding = getEncoding(outputMessage.getHeaders().getContentType());
        JsonGenerator jsonGenerator =
                this.objectMapper.getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);
        try {
            if (this.prefixJson) {
                jsonGenerator.writeRaw("{} && ");
            }
            this.objectMapper.writeValue(jsonGenerator, o);
        } catch (JsonGenerationException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    private JsonEncoding getEncoding(MediaType contentType) {
        if (contentType != null && contentType.getCharSet() != null) {
            Charset charset = contentType.getCharSet();
            for (JsonEncoding encoding : JsonEncoding.values()) {
                if (charset.name().equals(encoding.getJavaName())) {
                    return encoding;
                }
            }
        }
        return JsonEncoding.UTF8;
    }
}