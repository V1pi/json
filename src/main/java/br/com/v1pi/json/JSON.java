/*
 * The MIT License
 *
 * Copyright 2017 V1pi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.v1pi.json;

import br.com.v1pi.json.annotations.JS;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author V1pi
 * @param <T> É tipo do objeto a ser transformado em JSON
 */
public class JSON <T> {
    private static final int ARRAY = 2107;
    
    private String strJSON = "";
    private T obj = null;
    private Class<T> c = null;
    
    /**@param obj objeto da classe a ser transformada em JSON*/
    public JSON(T obj){
        c = (Class<T>) obj.getClass();
        this.obj = obj;
    }
    
    /**Contrutor vazio, caso queira passar o objeto depois utilizar o setObject(T obj)*/
    public JSON(){
    
    }
    
    /**
     * Metódo para transformar um string em JSON
     * @return String em formato JSON
     */
    public String toJSON(){
        if(c == null || obj == null)
            return "Por favor informe o objeto da classe a ser transformado em JSON!";
        
        strJSON = "{";
        
        if(!HasJSAnnotations())  UseAttributeName();          
        
        
        strJSON += "}";
        
        return strJSON;
    }
    
    /** Método para criar o JSON usando o proprio nome dos campos*/
    private void UseAttributeName(){
        List<Field> fields = Arrays.asList(c.getDeclaredFields()); 
        
        for(int i =0; i < fields.size(); i++){
            final boolean isTheLast = (fields.size()-1 == i);
            Field f = fields.get(i);

            f.setAccessible(true);

            if(IsDifferentTypeData(f, f.getName())) continue;               

            FormateAndCreateJSON(f, f.getName(), isTheLast, 0);
        }        
    }
    
    /**Verificar se tem alguma annotation JSON, se não tiver usar todos os atributos com o proprio nome*/
    private boolean HasJSAnnotations(){        
        List<Field> fields = Arrays.asList(c.getDeclaredFields()); 
        
        //Se o primero valor não tiver a annotation ele ja retorna false para utilizar o nome do atributo, 
        //mas se tiver um verdade ele já realizar com a annotation name
        if (Arrays.asList(fields.get(0).getDeclaredAnnotationsByType(JS.class)).isEmpty()) return false;

        for(int i =0; i < fields.size(); i++){
            final boolean isTheLast = (fields.size()-1 == i);
            Field f = fields.get(i);

            List<JS> js = Arrays.asList(f.getAnnotationsByType(JS.class));
            if(js.isEmpty())
                continue;
            
            //Coloca o valor do campo para acessível caso fosse private
            f.setAccessible(true);
            
            //Verifica se é um tipo diferente caso for ele executa um codigo especial e pula
            //a ultima instrução
            if(IsDifferentTypeData(f, js.get(0).value())) continue;               
            
            //Realiza caso o dado for um tipo primitivo
            FormateAndCreateJSON(f, js.get(0).value(), isTheLast, 0);
        }        
        return true;
    }
    /** Configura a classe para o tipo que desejar
     * @param obj objeto a ser utilizado na classe
     */
    public void setObject(T obj){
        this.obj = obj;
        c = (Class<T>) obj.getClass();
    
    }
    /* Cria um JSON para array
    *@param l lista a ser recebida para cirar o array*/
    private void CreateJSONArray(List<?> l){
        for(int i =0; i < l.size(); i++){                             
            Object o = l.get(i);
            //Caso o object seja uma ista de uma lista ele chama o proprio metodo passando a lista correspondente
            if(o.getClass() == Arrays.class || o.getClass() == List.class || o.getClass() == ArrayList.class){
                    
                strJSON += "[";

                CreateJSONArray((List) o);

                strJSON += "]";
            }    
            String format = (o.getClass() == String.class || o.getClass() == char.class) ? "\"%s\"" : "%s";
            format = (i == l.size()-1) ? format : format + ",";
            strJSON += String.format(format, o.toString());                        
        }
    }
    /** Verifica se o tipo de campo é especial
     * @param field campo a ser verificado se é de algum tipo especial (Ex: array, outra classe)
     * @param name nome do campo
     * @param isTheLast se é a ultima repetição do for*/
    private boolean IsDifferentTypeData(Field f, String name){
        //Verificar se é algum tipo de array ou lista
         if(f.getType() == Arrays.class || f.getType() == List.class || f.getType() == ArrayList.class){
                strJSON += String.format("\"%s\":[", name);
                
                //Passo o valor false para 'isTheLast' contudo é irrelevante esse valor para um array,
                //visto que o Create array que vai manipular a ultima virgula
                FormateAndCreateJSON(f, null, false, ARRAY);

                strJSON += " ]";
                return true;
            }         
        return false;
    }
    /** Formata e cria o JSON da classe
     @param f campo da classe a ser transformado em JSON
     @param name nome do campo
     @param isTheLast se é a última repetição do laço
     @param type se é algum tipo especial*/
    private void FormateAndCreateJSON(Field f,String name ,boolean isTheLast, int type){
        //Verifica se o tipo é char ou String para por as aspas
        String format = (f.getType() == String.class || f.getType() == char.class) ? "\"%s\":\"%s\"" : 
                "\"%s\":%s";
        
                            
        //Vê se o objeto passado não é de algum tipo especial, no caso ARRAY
        if(type != 0){
            switch(type){
                case ARRAY:   
                    try {
                        //Cria uma lista do array
                        List l = (List) f.get(obj);
                        //Chama o método CreateJSONArray para criar um JSON de um array
                        CreateJSONArray(l);
                        
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
                        return;
                    }                   
                    
                    break;
            }
                        
            return;
        }            
        
        //Se não houver um name retorna
        if(name==null) return;
        
        //Verifica se é ou não a ultima vez do for, para por a virgula
        format = isTheLast ? format : format + ",";

        try {
            //Cria o JSON com o formato especificado, primerio o nome depois o valor
            strJSON += String.format(format, name, f.get(obj).toString());
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}
