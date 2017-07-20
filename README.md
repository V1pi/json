# JSON
## Metas iniciais

Há milhares de bibliotecas JSON por todos os lados, e cada uma tem seus motivos para existir. Contudo, esta classe tem:

- **Integração Trivial**. Apenas instanciando uma classe usar métodos JSON que ajudem nas operações usuais dos programadores.
- **Extremamente Simples**. Realizando somentes as operações mais simples. toJSON(), fromJSON().
- **Flexibilidade**. Utilizando ou não as Annotations transformar os atributos em JSON.

##Utilizando

Nesta versão que se encontra. Suponha uma classe PESSOA, com atributos nome(String), idade(int). Você poderá transformar seus objetos em JSON fazendo:

```java
#main.java

import br.com.v1pi.json.JSON;
import br.exemplo.Pessoa;

public class main{

	public static void main(String[] args){
		//Cria o objeto Pessoa
		Pessoa p = new Pessoa();
		p.setNome("Vinicius");
		p.setIdade(20);
		
		//Classe JSON
		JSON<Pessoa> j = new JSON<>(p);
		
		
		/* Ou poderia
		JSON<Pessoa> j = new JSON();
		j.setObject(p);*/
		
		System.out.println(j.toJSON());
		//Saida: {"nome":"Vinicius","idade":20}
	}

}
```

Caso queira personalizar o nome do JSON, utilize a Annotation JS na CLASSE a ser usada:

```java
#Pessoa.java

import br.com.v1pi.annotations.JS;

public class Pessoa {

	@JS("nome_pessoa")
    private String nome;
	@JS("idade_pessoa")
    private int idade;
	
	public void setNome(String nome) {
        this.nome = nome;
    }
	
	public void setIdade(int idade) {
        this.idade = idade;
    }
}

```

Na classe main:

```java
#main.java

package package br.com.v1pi.json;

public class main{

	public static void main(String[] args){
		//Cria o objeto Pessoa
		Pessoa p = new Pessoa();
		p.setNome("Vinicius");
		p.setIdade(20);
		
		//Classe JSON
		JSON<Pessoa> j = new JSON<>(p);
		
		
		/* Ou poderia
		JSON<Pessoa> j = new JSON();
		j.setObject(p);*/
		
		System.out.println(j.toJSON());
		//Saida: {"nome_pessoa":"Vinicius","idade_idade":20}
	}

}
```

##Finalizando

Esse projeto tem licença MIT, então poderá ser utilizado comercialmente e é aberto.

Idealizador: Vinícius Picanço