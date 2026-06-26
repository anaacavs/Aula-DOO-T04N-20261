# Null Safety em Kotlin

**Nome:** _(preencher)_

**Conceito escolhido:** Null Safety (segurança contra nulos)

**Timestamp do vídeo que menciona o conceito:** 00:48 — _"No more null pointer exceptions with nullable data types"_

---

## O que é?

Null Safety é um recurso do Kotlin que ajuda a evitar erros causados por valores nulos (`null`). Por padrão, no Kotlin uma variável **não pode** receber `null`. Se você quiser permitir nulo, precisa avisar isso colocando um `?` no tipo.

```kotlin
var nome: String = "Ana"    // nunca pode ser null
var apelido: String? = null  // pode ser null (por causa do ?)
```

## Pra que serve?

Serve para evitar o erro mais famoso da programação: o `NullPointerException` (quando o programa tenta usar algo que está nulo e quebra). O Kotlin obriga você a pensar nos casos de nulo antes de rodar o programa, então muitos desses erros são pegos já na hora de escrever o código.

## Como é normalmente utilizado?

Você marca o tipo com `?` quando ele pode ser nulo, e para acessá-lo com segurança usa a chamada segura `?.`. Se o valor for nulo, em vez de quebrar, o resultado simplesmente vira `null`.

```kotlin
val apelido: String? = null
println(apelido?.length)   // imprime "null" em vez de quebrar o programa
```

## Exemplo de código

```kotlin
fun main() {
    val nome: String? = null

    // Acesso seguro: se for null, não quebra
    println(nome?.uppercase())   // imprime: null

    // Mostra o tamanho do nome, ou 0 se estiver nulo
    val tamanho = nome?.length ?: 0
    println(tamanho)             // imprime: 0
}
```

Resumindo: o `?` diz que algo **pode** ser nulo, e o `?.` permite usar esse algo **com segurança**, sem o programa travar. É por isso que a música diz "no more null pointer exceptions".
