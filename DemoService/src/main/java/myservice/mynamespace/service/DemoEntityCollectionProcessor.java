package myservice.mynamespace.service;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.sql.*;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

public class DemoEntityCollectionProcessor implements EntityCollectionProcessor {

	private OData odata;
	private ServiceMetadata serviceMetadata;
	
	@Override
	public void init(OData odata, ServiceMetadata serviceMetadata) {
		
		this.odata = odata;
		this.serviceMetadata = serviceMetadata;
	}

	@Override
	public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
		 
		// 1st we have retrieve the requested EntitySet from the uriInfo object (representation of the parsed service URI)
		  List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		  UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the first segment is the EntitySet
		  EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

		  // 2nd: fetch the data from backend for this requested EntitySetName
		  // it has to be delivered as EntitySet object
		  EntityCollection entitySet = getData(edmEntitySet);

		  // 3rd: create a serializer based on the requested format (json)
		  ODataSerializer serializer = odata.createSerializer(responseFormat);

		  // 4th: Now serialize the content: transform from the EntitySet object to InputStream
		  EdmEntityType edmEntityType = edmEntitySet.getEntityType();
		  ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

		  final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
		  EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
		  SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet, opts);
		  InputStream serializedContent = serializerResult.getContent();

		  // Finally: configure the response object: set the body, headers and status code
		  response.setContent(serializedContent);
		  response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		  response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());

	}
	
	private EntityCollection getData(EdmEntitySet edmEntitySet) {

		   try {
		   
		   EntityCollection productsCollection = new EntityCollection();
		   // check for which EdmEntitySet the data is requested
		   if(DemoEdmProvider.ES_PRODUCTS_NAME.equals(edmEntitySet.getName())) {
		       List<Entity> productList = productsCollection.getEntities();

		       //setup a JDBC connection
		       Class.forName("oracle.bi.jdbc.AnaJdbcDriver");
			   Connection con = DriverManager.getConnection("jdbc:oraclebi:https://instance1-bm1xf5heqnjp-bo.analytics.ocp.oraclecloud.com:443/api/jdbc?BIJDBC_PROPERTIES_FILE=C:\\\\Oracle\\\\Middleware\\\\Oracle_Home\\\\bi\\\\bifoundation\\\\jdbc\\\\bijdbc.properties");
			   Statement st = con.createStatement();
		       ResultSet rs = st.executeQuery("select * from Product");
		       
		       while (rs.next()) {
		    	   
		       // add some sample product entities
		       final Entity e = new Entity()
		          .addProperty(new Property(null, "ProductId", ValueType.PRIMITIVE, rs.getInt("ProductID")))
		          .addProperty(new Property(null, "ProductName", ValueType.PRIMITIVE,rs.getString("ProductName")))
		          .addProperty(new Property(null, "SupplierId", ValueType.PRIMITIVE, rs.getInt("SupplierID")))
		          .addProperty(new Property(null, "CategoryId", ValueType.PRIMITIVE, rs.getInt("CategoryID")))
		          .addProperty(new Property(null, "QuantityPerUnit", ValueType.PRIMITIVE, rs.getString("QuantityPerUnit")))
		          .addProperty(new Property(null, "UnitPrice", ValueType.PRIMITIVE, rs.getDouble("UnitPrice")))
		          .addProperty(new Property(null, "UnitsInStock", ValueType.PRIMITIVE, rs.getInt("UnitsInStock")))
		          .addProperty(new Property(null, "ReorderLevel", ValueType.PRIMITIVE, rs.getInt("ReorderLevel")))
		          .addProperty(new Property(null, "Discontinued", ValueType.PRIMITIVE, rs.getInt("Discontinued")))
		          .addProperty(new Property(null, "Revenue", ValueType.PRIMITIVE, rs.getDouble("Revenue")))
		          .addProperty(new Property(null, "QtySold", ValueType.PRIMITIVE, rs.getInt("QtySold")))
		          .addProperty(new Property(null, "UnitsOnOrder", ValueType.PRIMITIVE, rs.getInt("UnitsOnOrder")));
		      e.setId(createId("Products", 1));
		      productList.add(e);
		      
		     }
		   }
		   
		   return productsCollection;
		   
		   } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
		  }
		}
	
	private URI createId(String entitySetName, Object id) {
	    try {
	        return new URI(entitySetName + "(" + String.valueOf(id) + ")");
	    } catch (URISyntaxException e) {
	        throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
	    }
	}

}
