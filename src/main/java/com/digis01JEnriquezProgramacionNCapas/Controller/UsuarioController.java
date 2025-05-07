package com.digis01JEnriquezProgramacionNCapas.Controller;

import com.digis01JEnriquezProgramacionNCapas.ML.Colonia;
import com.digis01JEnriquezProgramacionNCapas.ML.Direccion;
import com.digis01JEnriquezProgramacionNCapas.ML.Estado;
import com.digis01JEnriquezProgramacionNCapas.ML.Municipio;

import com.digis01JEnriquezProgramacionNCapas.ML.Pais;
import com.digis01JEnriquezProgramacionNCapas.ML.Result;
import com.digis01JEnriquezProgramacionNCapas.ML.ResultFile;
import com.digis01JEnriquezProgramacionNCapas.ML.ResultWeb;
import com.digis01JEnriquezProgramacionNCapas.ML.Rol;
import com.digis01JEnriquezProgramacionNCapas.ML.Usuario;
import com.digis01JEnriquezProgramacionNCapas.ML.UsuarioDireccion;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private RestTemplate restTemplate = new RestTemplate();
    private String baseUrl = "http://localhost:8081/";

    @GetMapping
    public String Index(Model model) {

        try {
            ResponseEntity<Result<UsuarioDireccion>> responseEntity = restTemplate.exchange(baseUrl + "usuario",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<UsuarioDireccion>>() {
            });
            //Obtener lista de Roles
            ResponseEntity<Result<Rol>> responseEntityRol = restTemplate.exchange(baseUrl + "rol/getAll",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Rol>>() {
            });

            Result result = responseEntity.getBody();
            Result resultRol = responseEntityRol.getBody();

            Usuario usuarioBusqueda = new Usuario();
            usuarioBusqueda.Rol = new Rol();

            model.addAttribute("usuarioBusqueda", usuarioBusqueda);
            model.addAttribute("listaUsuarios", result.objects);
            model.addAttribute("listaRol", resultRol.objects);

            return "Index";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("descripcionError", ex);
            model.addAttribute("statusCode", ex.getStatusCode());
            return "Error";
        }
    }

    @PostMapping("/GetAllDinamico")
    public String BusquedaDinamica(@ModelAttribute Usuario usuario, Model model) {
        try {
            //Obtener UsuarioDireccion
            ResponseEntity<Result<UsuarioDireccion>> responseEntity = restTemplate.exchange(baseUrl + "usuario/getAllDinamico",
                    HttpMethod.POST,
                    new HttpEntity<>(usuario),
                    new ParameterizedTypeReference<Result<UsuarioDireccion>>() {
            });

            //Obtener lista de Roles
            ResponseEntity<Result<Rol>> responseEntityRol = restTemplate.exchange(baseUrl + "rol/getAll",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<Rol>>() {
            });

            Result result = responseEntity.getBody();
            Result resultRol = responseEntityRol.getBody();

            Usuario usuarioBusqueda = new Usuario();
            usuarioBusqueda.Rol = new Rol();

            model.addAttribute("listaUsuarios", result.objects);
            model.addAttribute("listaRol", resultRol.objects);
            model.addAttribute("usuarioBusqueda", usuarioBusqueda);

            return "Index";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("statusCode", ex.getStatusCode());
            model.addAttribute("descripcionError", ex);
            return "Error";
        }
    }

    @GetMapping("Form/{IdUsuario}")
    public String Form(@PathVariable int IdUsuario, Model model) {
        if (IdUsuario == 0) {
            try {
                //Obtener lista de Roles
                ResponseEntity<Result<Rol>> responseEntityRol = restTemplate.exchange(baseUrl + "rol/getAll",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Rol>>() {
                });

                //Obtener lista de Paises
                ResponseEntity<Result<Pais>> responseEntityPais = restTemplate.exchange(baseUrl + "pais/getAll",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Pais>>() {
                });

                Result result = responseEntityRol.getBody();
                Result resultPais = responseEntityPais.getBody();

                UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
                usuarioDireccion.Usuario = new Usuario();
                usuarioDireccion.Usuario.Rol = new Rol();
                usuarioDireccion.Direccion = new Direccion();
                usuarioDireccion.Direccion.Colonia = new Colonia();

                model.addAttribute("listaRol", result.objects);
                model.addAttribute("listaPais", resultPais.objects);
                model.addAttribute("usuarioDireccion", usuarioDireccion);

                return "UsuarioForm";
            } catch (HttpStatusCodeException ex) {
                model.addAttribute("statusCode", ex.getStatusCode());
                model.addAttribute("descripcionError", ex);

                return "Error";
            }

        } else {
            try {
//                varios Parametros
                Map<String, Object> uriVariable = new HashMap<>();
                uriVariable.put("IdUsuario", IdUsuario);

                ResponseEntity<Result<UsuarioDireccion>> responseEnrityGetUsuarioDireccionById = restTemplate.exchange(baseUrl + "usuario/getAllById/{IdUsuario}",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<UsuarioDireccion>>() {
                },
                        uriVariable);

                Result result = responseEnrityGetUsuarioDireccionById.getBody();
                model.addAttribute("listaUsuario", result.object);
                return "UsuarioView";

            } catch (HttpStatusCodeException ex) {
                model.addAttribute("statusCode", ex.getStatusCode());
                model.addAttribute("descripcionError", ex);
                return "Error";
            }
        }

    }

    @GetMapping("/FormView")
    public String FormView(Model model, @RequestParam int IdUsuario, @RequestParam(required = false) Integer IdDireccion) {
        if (IdUsuario > 0 && IdDireccion == 0) {
            try {
                ResponseEntity<Result<Pais>> responseEntityPais = restTemplate.exchange(baseUrl + "pais/getAll",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Pais>>() {
                });

                Result resultPais = responseEntityPais.getBody();

                //Agrega una direccion
                UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
                usuarioDireccion.Usuario = new Usuario();
                usuarioDireccion.Usuario.setIdUsuario(IdUsuario);
                usuarioDireccion.Direccion = new Direccion();
                usuarioDireccion.Direccion.setIdDireccion(0);
                usuarioDireccion.Direccion.Colonia = new Colonia();

                model.addAttribute("listaPais", resultPais.correct ? resultPais.objects : null);
                model.addAttribute("usuarioDireccion", usuarioDireccion);
            } catch (HttpStatusCodeException ex) {
                model.addAttribute("statusCode", ex.getStatusCode());
                model.addAttribute("descripcionError", ex);
                return "Error";
            }

        } else if (IdUsuario > 0 && IdDireccion > 0) {
            //Editar direccion
            try {
                ResponseEntity<Result<Direccion>> responseEntityDireccion = restTemplate.exchange(baseUrl + "direccion/getById/{IdDireccion}",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Direccion>>() {
                },
                        IdDireccion);

                Result resultDireccion = responseEntityDireccion.getBody();

                UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
                usuarioDireccion.Usuario = new Usuario();
                usuarioDireccion.Usuario.setIdUsuario(IdUsuario);
                usuarioDireccion.Direccion = new Direccion();
                usuarioDireccion.Direccion.setIdDireccion(IdDireccion);

                usuarioDireccion.Direccion = (Direccion) resultDireccion.object;

                ResponseEntity<Result<Pais>> responseEntityPais = restTemplate.exchange(baseUrl + "pais/getAll",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Pais>>() {
                });

                ResponseEntity<Result<Estado>> responseEntityEstado = restTemplate.exchange(baseUrl + "estado/byIdPais/{IdPais}",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Estado>>() {
                },
                        usuarioDireccion.Direccion.Colonia.Municipio.Estado.Pais.getIdPais());

                ResponseEntity<Result<Municipio>> responseEntityMunicipio = restTemplate.exchange(baseUrl + "municipio/byIdEstado/{IdEstado}",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Municipio>>() {
                },
                        usuarioDireccion.Direccion.Colonia.Municipio.Estado.getIdEstado());

                ResponseEntity<Result<Colonia>> responseEntityColonia = restTemplate.exchange(baseUrl + "colonia/byIdMunicipio/{IdMunicipio}",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Colonia>>() {
                },
                        usuarioDireccion.Direccion.Colonia.Municipio.getIdMunicipio());

                Result resultPais = responseEntityPais.getBody();
                Result resultEstado = responseEntityEstado.getBody();
                Result resultMunicipio = responseEntityMunicipio.getBody();
                Result resultColonia = responseEntityColonia.getBody();

                model.addAttribute("listaPais", resultPais.correct ? resultPais.objects : null);
                model.addAttribute("listaEstados", resultEstado.objects);
                model.addAttribute("listaMunicipio", resultMunicipio.objects);
                model.addAttribute("listaColonia", resultColonia.objects);
                model.addAttribute("usuarioDireccion", usuarioDireccion);

            } catch (HttpStatusCodeException ex) {
                model.addAttribute("statusCode", ex.getStatusCode());
                model.addAttribute("descripcionError", ex);
                return "Error";
            }

        } else {
            //Edita un usuario
            try {
                Map<String, Object> uriVariable = new HashMap<>();
                uriVariable.put("IdUsuario", IdUsuario);

                //Obtiene el usuario por Id
                ResponseEntity<Result<UsuarioDireccion>> responseEntityUsuario = restTemplate.exchange(baseUrl + "usuario/getById/{IdUsuario}",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<UsuarioDireccion>>() {
                },
                        uriVariable);

                //obtener Lista de Roles
                ResponseEntity<Result<Rol>> responseEntityRol = restTemplate.exchange(baseUrl + "rol/getAll",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<Result<Rol>>() {
                });

                Result result = responseEntityRol.getBody();
                Result resultUsuario = responseEntityUsuario.getBody();

                UsuarioDireccion usuarioDireccion = new UsuarioDireccion();
                usuarioDireccion = (UsuarioDireccion) resultUsuario.object;

                usuarioDireccion.Direccion = new Direccion();
                usuarioDireccion.Direccion.setIdDireccion(IdDireccion);
                model.addAttribute("usuarioDireccion", usuarioDireccion);
                model.addAttribute("listaRol", result.objects);

            } catch (HttpStatusCodeException ex) {
                model.addAttribute("statusCode", ex.getStatusCode());
                model.addAttribute("descripcionError", ex);
                return "Error";
            }
        }

        return "UsuarioForm";
    }

    @GetMapping("/DeleteDireccion")
    public String DeleteDireccion(@RequestParam int IdDireccion, Model model) {

        try {
            ResponseEntity<Result> responseEntity = restTemplate.exchange(baseUrl + "direccion/delete/{IdDireccion}",
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            },
                    IdDireccion);

            Result result = responseEntity.getBody();

            return "redirect:/usuario";
        } catch (HttpStatusCodeException ex) {
            model.addAttribute("statusCode", ex.getStatusCode());
            model.addAttribute("descripcionError", ex);
            return "Error";
        }
    }

    @GetMapping("/DeleteUsuario")
    public String DeleteUsuario(@RequestParam int IdUsuario, Model model) {
        try {
            ResponseEntity<Result> responseEntity = restTemplate.exchange(baseUrl + "usuario/delete/{IdUsuario}",
                    HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            },
                    IdUsuario);

            Result result = responseEntity.getBody();

        } catch (HttpStatusCodeException ex) {
            model.addAttribute("statusCode", ex.getStatusCode());
            model.addAttribute("descripcionError", ex);
            return "Error";
        }

        return "redirect:/usuario";
    }

    @PostMapping("Form")
    public String Form(@Valid @ModelAttribute UsuarioDireccion usuarioDireccion, BindingResult BindingResult, @RequestParam(required = false) MultipartFile imagenFile, Model model) {
//        if(BindingResult.hasErrors()){
//            model.addAttribute("listaUsuario", usuarioDireccion);
//            
//            return "UsuarioForm";
//        }
        usuarioDireccion.Usuario.setStatus(1);
        try {
            if (!imagenFile.isEmpty()) {
                byte[] bytes = imagenFile.getBytes();
                String imgBase64 = Base64.getEncoder().encodeToString(bytes);
                usuarioDireccion.Usuario.setImagen(imgBase64);

            }
        } catch (Exception ex) {

        }
        if (usuarioDireccion.Usuario.getIdUsuario() > 0 && usuarioDireccion.Direccion.getIdDireccion() == 0) {
            //Agregar una direccion

            ResponseEntity<Result> responseEntityDireccionAdd = restTemplate.exchange(baseUrl + "direccion/add",
                    HttpMethod.POST,
                    new HttpEntity<>(usuarioDireccion),
                    new ParameterizedTypeReference<Result>() {
            });

            if (responseEntityDireccionAdd.getStatusCode().value() != 200) {
                model.addAttribute("statusCode", responseEntityDireccionAdd.getStatusCode().value());
                model.addAttribute("descripcionError", responseEntityDireccionAdd.getBody());
                return "Error";
            }

        } else {
            if (usuarioDireccion.Usuario.getIdUsuario() > 0 && usuarioDireccion.Direccion.getIdDireccion() > 0) {
                //Editar Direccion
                //direccionDAOImplementation.DireccionUpdate(usuarioDireccion.Direccion);
//                direccionDAOImplementation.DireccionUpdateJPA(usuarioDireccion.Direccion);
            } else if (usuarioDireccion.Usuario.getIdUsuario() > 0 && usuarioDireccion.Direccion.getIdDireccion() == -1) {
                //Editar usuario
                //usuarioDAOImplementation.UsuarioUpdate(usuarioDireccion.Usuario);
                try {

                    ResponseEntity<Result> responseEntityUpdateUsuario = restTemplate.exchange(baseUrl + "usuario/update",
                            HttpMethod.PUT,
                            new HttpEntity<>(usuarioDireccion.Usuario),
                            new ParameterizedTypeReference<Result>() {
                    });

                    Result result = responseEntityUpdateUsuario.getBody();
                } catch (HttpStatusCodeException ex) {
                    model.addAttribute("statusCode", ex.getStatusCode());
                    model.addAttribute("descripcionError", ex);
                    return "Error";
                }

            } else {
                //Agregar Usuario y Direccion
                //usuarioDAOImplementation.Add(usuarioDireccion);
//                usuarioDAOImplementation.AddJPA(usuarioDireccion);
            }
        }
        return ("redirect:/usuario");
    }

    @GetMapping("/CargaMasiva")
    public String CargaMsiva() {
        return "CargaMasiva";
    }

    @PostMapping("/CargaMasiva")
    public String CargaMasiva(@RequestParam(value = "archivo") MultipartFile archivo, Model model, HttpSession session) {

        try {
            if (archivo != null && !archivo.isEmpty()) {
                //ByteArrayResource byteArrayResource = new ByteArrayResource(archivo.getBytes());
                
                Resource invoicesResource = archivo.getResource();
                
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("archivo", invoicesResource);

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

                HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(body, httpHeaders);

                ResponseEntity<Result> responseEntity = restTemplate.exchange(baseUrl + "usuario/cargaMasiva",
                        HttpMethod.POST,
                        httpEntity,
                        new ParameterizedTypeReference<Result>() {
                });

                Result result = responseEntity.getBody();

                if (responseEntity.getStatusCode().value() == 200) {
                    session.setAttribute("urlFile", result.object);
                    //model.addAttribute("listaErrores", resultFile.listaErrores);
                    model.addAttribute("success", true);
                }
            }
        } catch (Exception ex) {
            return "redirect:/usuario/CargaMasiva";
        }
        return "CargaMasiva";
    }

//    @GetMapping("/Procesar")
//    public String Procesar(HttpSession session) {
//        String absolutePath = session.getAttribute("urlFile").toString();
//        String tipoArchivo = session.getAttribute("urlFile").toString().split("\\.")[1];
//        List<UsuarioDireccion> listaUsuarios = new ArrayList<>();
//
//        if (tipoArchivo.equals("txt")) {
//             listaUsuarios = LecturaArchivoTXT(new File(absolutePath));
//        }else{
//            listaUsuarios = LecturaArchivoExcel(new File(absolutePath));
//        }        
//        
//        for (UsuarioDireccion usuarioDireccion : listaUsuarios) {
//            //usuarioDAOImplementation.Add(usuarioDireccion);
//            usuarioDAOImplementation.AddJPA(usuarioDireccion);
//        }
//        return "CargaMasiva";
//    }
}
