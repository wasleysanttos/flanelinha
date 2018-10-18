package util;


import util.Constantes;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.example.wasleysantos.flanelinha.model.TipoVaga;
import com.example.wasleysantos.flanelinha.model.Usuario;
import com.example.wasleysantos.flanelinha.model.Vaga;


/**
 * Created by wasleysantos on 30/08/2016.
 */
public class RequisicaoHttp {


    //cadastrar usuario
    public Usuario cadastrarUsuarioWebService (Usuario stringUsuario) throws IOException {

        final String WEBSERVICE_URL = Constantes.SERVIDOR + "/cadastrarUsuario.php";

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();
        String userJSONString = gson.toJson(stringUsuario);
        RequestBody body = RequestBody.create(mediaType, userJSONString);
        Request postRequest = new Request.Builder()
                .url(WEBSERVICE_URL)
                .post(body)
                .build();

        Response postResponse = client.newCall(postRequest).execute();
        String json = postResponse.body().string();

        return gson.fromJson(json, Usuario.class);
    }

    //consultar usuario
    public Usuario consultarUsuarioWebService (Usuario stringUsuario) throws IOException {
        final String WEBSERVICE_URL;
        if (stringUsuario.getId() == 0){
            WEBSERVICE_URL =  Constantes.SERVIDOR + "/consultarUsuario.php";
        } else {
            WEBSERVICE_URL =  Constantes.SERVIDOR + "/login.php";
        }

        OkHttpClient client = new OkHttpClient();

        try {
            client.setReadTimeout(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            client.setConnectTimeout(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();

        }


        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();
        String userJSONString = gson.toJson(stringUsuario);
        RequestBody body = RequestBody.create(mediaType, userJSONString);

        Request postRequest = new Request.Builder()
                .url(WEBSERVICE_URL)
                .post(body)
                .build();

        Response postResponse = client.newCall(postRequest).execute();

        String json = postResponse.body().string();

        if (json.length() > 2){
            return gson.fromJson(json, Usuario.class);
        }

        return null;
    }

    //alterar usuario
    public Usuario alterarUsuarioWebService (Usuario stringUsuario) throws IOException {

        final String WEBSERVICE_URL =  Constantes.SERVIDOR + "/alterarUsuario.php";

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();
        String userJSONString = gson.toJson(stringUsuario);
        RequestBody body = RequestBody.create(mediaType, userJSONString);
        Request postRequest = new Request.Builder()
                .url(WEBSERVICE_URL)
                .post(body)
                .build();

        Response postResponse = client.newCall(postRequest).execute();
        String json = postResponse.body().string();

        return gson.fromJson(json, Usuario.class);

    }



    //lista de vagas
    public ArrayList<Vaga> consultaVagasWebService() throws Exception{
        final String WEBSERVICE_URL =  Constantes.SERVIDOR + "/consultarVagas.php";

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);

        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url(WEBSERVICE_URL)
                .get()
                .build();

            Response response = client.newCall(request).execute();
            String json = response.body().string();

            Type collectionType = new TypeToken<ArrayList<Vaga>>(){}.getType();
            ArrayList<Vaga> listaVaga = new ArrayList<>();
            listaVaga = gson.fromJson(json, collectionType);


            return listaVaga;

    }

    //ocupar e liberar vaga
    public void ocuparVagaWebService (Vaga stringVaga) throws IOException {
        final String WEBSERVICE_URL;
        if (stringVaga.getStatus() != 0){
           WEBSERVICE_URL =  Constantes.SERVIDOR + "/ocuparVaga.php";
        }
        else {
            WEBSERVICE_URL =  Constantes.SERVIDOR + "/liberarVaga.php";
        }


        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        String vagaJSONString = gson.toJson(stringVaga);

        RequestBody body = RequestBody.create(mediaType, vagaJSONString);
        Request postRequest = new Request.Builder()
                .url(WEBSERVICE_URL)
                .post(body)
                .build();

        Response postResponse = client.newCall(postRequest).execute();
        postResponse.body().string();

    }

    //lista historico do usuario
    public ArrayList<Vaga> consultaVagasUsuarioWebService(Usuario usuario) throws Exception{
        final String WEBSERVICE_URL =  Constantes.SERVIDOR + "/historicoVagas.php";

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gsonUsuario = new Gson();

        Gson gsonLista = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        String usuarioJSONString = gsonUsuario.toJson(usuario);

        RequestBody body = RequestBody.create(mediaType, usuarioJSONString);
        Request request = new Request.Builder()
                .url(WEBSERVICE_URL)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String json = response.body().string();

        Type collectionType = new TypeToken<ArrayList<Vaga>>(){}.getType();
        ArrayList<Vaga> listaVaga = new ArrayList<>();
        listaVaga = gsonLista.fromJson(json, collectionType);


        return listaVaga;

    }

    //lista vagas ocupadas pelo usuario
    public ArrayList<Vaga> consultaVagasOcupadasUsuarioWebService(Usuario usuario) throws Exception{
        final String WEBSERVICE_URL =  Constantes.SERVIDOR + "/historicoOcupacaoVagas.php";

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gsonUsuario = new Gson();

        Gson gsonLista = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        String usuarioJSONString = gsonUsuario.toJson(usuario);

        RequestBody body = RequestBody.create(mediaType, usuarioJSONString);
        Request request = new Request.Builder()
                .url(WEBSERVICE_URL)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String json = response.body().string();

        Type collectionType = new TypeToken<ArrayList<Vaga>>(){}.getType();
        ArrayList<Vaga> listaVaga = new ArrayList<>();
        listaVaga = gsonLista.fromJson(json, collectionType);


        return listaVaga;

    }

    //cadastrar vaga
    public void criarVagaWebService (Vaga stringVaga) throws IOException {

//        SimpleDateFormat formatarDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        formatarDate.format(stringVaga.getData_vaga());

//        stringVaga.setData_vaga();

        final String WEBSERVICE_URL =  Constantes.SERVIDOR + "/criarVaga.php";

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        String vagaJSONString = gson.toJson(stringVaga);
        RequestBody body = RequestBody.create(mediaType, vagaJSONString);
        Request postRequest = new Request.Builder()
                .url(WEBSERVICE_URL)
                .post(body)
                .build();

        Response postResponse = client.newCall(postRequest).execute();
        postResponse.body().string();

    }

    //lista de tipos de vagas
    public ArrayList<TipoVaga> consultaTipoVagasWebService() throws Exception{
        final String WEBSERVICE_URL =  Constantes.SERVIDOR + "/listaTipoVaga.php";

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url(WEBSERVICE_URL)
                .build();


        Response response = client.newCall(request).execute();
        String json = response.body().string();




        Type collectionType = new TypeToken<ArrayList<TipoVaga>>() {}.getType();
        ArrayList<TipoVaga> listaTipoVaga = gson.fromJson(json, collectionType);

        return listaTipoVaga;

    }

    //lista de tipos de vagas
    public ArrayList<TipoVaga> consultaTipoVagasDisponiveisWebService() throws Exception{
        final String WEBSERVICE_URL =  Constantes.SERVIDOR + "/listaTiposDeVagasDisponiveis.php";

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url(WEBSERVICE_URL)
                .build();


        Response response = client.newCall(request).execute();
        String json = response.body().string();




        Type collectionType = new TypeToken<ArrayList<TipoVaga>>() {}.getType();
        ArrayList<TipoVaga> listaTipoVaga = gson.fromJson(json, collectionType);

        return listaTipoVaga;

    }


}
