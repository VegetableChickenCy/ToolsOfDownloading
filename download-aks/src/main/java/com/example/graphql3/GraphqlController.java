/*
package com.example.graphql3;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping(name = "graphql")
public class GraphqlController {

    @PostMapping("graphql")
    @ResponseBody
    public Object graphql(@RequestBody String query) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:graphql/root_schema.graphql");
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(file);
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                //UserQuery：schema中定义的查询类型名称
                .type("UserQuery", builder ->
                        //user：查询类型中对象类型的名称
                        builder.dataFetcher("findUser", environment -> {
                            List<TbUser> list = new ArrayList();
                            TbUser tbUser = new TbUser().setUserName("张三").setPassword("12341").setId("1").setCityName("成都");
                            TbUser tbUser2 = new TbUser().setUserName("李四").setPassword("4123556").setId("2").setCityName("北京");
                            TbUser tbUser3 = new TbUser().setUserName("王五").setPassword("12243").setId("3").setCityName("上海");
                            list.add(tbUser);
                            list.add(tbUser2);
                            list.add(tbUser3);
                            Iterator<TbUser> iterator = list.iterator();
                            while (iterator.hasNext()) {
                                TbUser next = iterator.next();
                                if(!StringUtils.isEmpty(environment.getArgument("id")) && !environment.getArgument("id").equals(next.getId())){
                                    iterator.remove();
                                }
                            }
                            // TbUser 随便建一个就行了
                            return list;
                        })
                ).build();
        GraphQLSchema qlSchema = new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);
        GraphQL graphql = GraphQL.newGraphQL(qlSchema).build();
        JSONObject jsonObject = JSON.parseObject(query);
        ExecutionResult execute = graphql.execute(jsonObject.getString("query"));
        Object data = execute.getData();
        return data;
    }
}
*/
