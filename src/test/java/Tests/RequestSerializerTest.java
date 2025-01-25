package Tests;

import org.filip.Request.GetRequest;
import org.filip.Request.OrderRequest;
import org.filip.Request.RegisterRequest;
import org.filip.Request.SetRequest;
import org.filip.parser.RequestSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestSerializerTest
{
    @Test
    void testSerializeGetRequest()
    {
        GetRequest request = new GetRequest("gp:", 123);
        String serialized = RequestSerializer.serializeRequest(request);
        assertEquals("gp:123", serialized);

        request = new GetRequest("gs:", 456);
        serialized = RequestSerializer.serializeRequest(request);
        assertEquals("gs:456", serialized);
    }

    @Test
    void testSerializeSetRequest()
    {
        SetRequest request = new SetRequest("sr:", 789);
        String serialized = RequestSerializer.serializeRequest(request);
        assertEquals("sr:789", serialized);

        request = new SetRequest("sj:", "localhost", 1011);
        serialized = RequestSerializer.serializeRequest(request);
        assertEquals("sj:localhost,1011", serialized);

        request = new SetRequest("spi:", 1213, 1415);
        serialized = RequestSerializer.serializeRequest(request);
        assertEquals("spi:1213,1415", serialized);

        request = new SetRequest("spo:", 1617);
        serialized = RequestSerializer.serializeRequest(request);
        assertEquals("spo:1617", serialized);
    }

    @Test
    void testSerializeRegisterRequest()
    {
        RegisterRequest request = new RegisterRequest("localhost", 8080);
        String serialized = RequestSerializer.serializeRequest(request);
        assertEquals("r:localhost,8080", serialized);
    }

    @Test
    void testSerializeOrderRequest() {
        OrderRequest request = new OrderRequest("localhost", 9090);
        String serialized = RequestSerializer.serializeRequest(request);
        assertEquals("o:localhost,9090", serialized);
    }
}