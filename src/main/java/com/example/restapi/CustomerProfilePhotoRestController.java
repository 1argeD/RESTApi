package com.example.restapi;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.util.concurrent.Callable;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(value="/customers/{id}/photo")
public class CustomerProfilePhotoRestController {
    private final File root;
    private final CustomerRepository customerRepository;
    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    CustomerProfilePhotoRestController(@Value("${upload.dir:${user.home}/images}") String uploadDir,
                                       CustomerRepository customerRepository) {
        this.root = new File(uploadDir);
        this.customerRepository = customerRepository;
        Assert.isTrue(this.root.exists() || this.root.mkdirs(),
                    String.format("The path '%s' must exist",
                            this.root.getAbsolutePath())
                );
    }

    @GetMapping
    ResponseEntity<Resource> read(@PathVariable("id") Long id) throws CustomerNotFoundException {
        return this.customerRepository.findById(id)
                .map(
                        customer -> {
                            File file = fileFor(customer);

                            Assert.isTrue(file.exists(),
                                    String.format("file-not-found $s",
                                            file.getAbsolutePath())
                                    );
                            Resource fileSystemResource = new FileSystemResource(file);
                            return ResponseEntity.ok()
                                    .contentType(MediaType.IMAGE_JPEG)
                                    .body(fileSystemResource);
                        }
                )
                .orElseThrow(CustomerNotFoundException::new);
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    Callable<ResponseEntity<?>> write(@PathVariable Long id,
                                   @RequestParam MultipartFile file){
        log.info(String.format("upload-start /customers/%s/photo (%s bytes)",
                id, file.getSize()));
        return () -> this.customerRepository
                .findById(id)
                .map(
                        customer -> {
                            File fileForCustomer = fileFor(customer);
                            try(InputStream in = file.getInputStream();
                                OutputStream put = new FileOutputStream(fileForCustomer)){

                            } catch(IOException ex) {
                            throw new RuntimeException((ex));
                            }
                            URI location = fromCurrentRequest()
                                    .buildAndExpand(id).toUri();
                            log.info(String .format("upload-finish /customer/%s/location", id, location));
                            return ResponseEntity.created(location).build();
                        }
                ).orElseThrow(CustomerNotFoundException::new);
    }

    private File fileFor(Customer person) {
        return new File(this.root, Long.toString(person.getId()));
    }
}
