# ShopHub — Trabalho 3 (Persistência com Room)

App Android (Kotlin + Jetpack Compose) para gerenciar **produtos** e **categorias**.
Evolução do Trabalho 2: as listas, formulários, detalhes e botões agora leem e gravam
em um banco **Room (SQLite)** em vez de `mutableStateListOf` em memória.

**Integrantes do trio:** _(preencher nomes)_
**Apresentação / slides:** _(colar aqui o link do Google Slides, se for usar)_

---

## Como rodar o projeto

1. Instale o **Android Studio** (versão recente, compatível com AGP 9.x) e um JDK 17+ (o embutido no Android Studio serve).
2. Clone o repositório e abra a pasta do projeto no Android Studio (`File > Open`).
3. Aguarde o **Gradle Sync** terminar (baixa o Room e o KSP automaticamente).
4. Crie/inicie um emulador (API 24+) ou conecte um celular com depuração USB.
5. Clique em **Run ▶** (módulo `app`).
6. Na tela de login, entre normalmente (a tela de login é a mesma dos trabalhos anteriores).

> O banco é criado no primeiro uso, com 3 categorias e 3 produtos iniciais (os mesmos do Trabalho 2).
> Para "zerar" o banco e testar do zero: `Settings > Apps > ShopHub > Storage > Clear data`.

---

## O que foi implementado (Room)

| Requisito | Onde está |
|---|---|
| Dependências Room + KSP | `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts` |
| 2 `@Entity` com `@PrimaryKey(autoGenerate = true)` | `model/Category.kt` (tabela `categorias`), `model/Product.kt` (tabela `produtos`) |
| 2 `@Dao` com `@Insert`, `@Query`, `@Update`, `@Delete` (todos `suspend`) | `data/CategoryDao.kt`, `data/ProductDao.kt` |
| Consultas filtradas | `buscarPorId` (os dois DAOs) e `ProductDao.buscarPorCategoria` |
| `AppDatabase` Singleton (`companion object` + `@Volatile` + `synchronized`) | `data/AppDatabase.kt` |
| Listas lendo do banco com `LaunchedEffect(Unit)` | `ProductListScreen`, `CategoryListScreen`, `HomeScreen` |
| Inserir | `AddProductScreen`, `AddCategoryScreen` (`dao.inserir`) |
| Atualizar (editar) | mesmos formulários em modo edição (`dao.atualizar`); também o checkbox Favorito/Destaque |
| Remover | ícone de lixeira nas listas (`dao.deletar`) |
| Detalhes lendo do banco pelo id | `ProductDetailScreen` e `CategoryDetailScreen` (recebem só o `id` pela navegação) |
| Operações de banco em `CoroutineScope(Dispatchers.IO).launch` + `try/catch` com `Log.e` | todas as telas acima |

### Como o fluxo de editar funciona
O formulário de adicionar foi **reaproveitado** para editar (em vez de criar uma tela nova):
- `AddProductScreen(productId = null)` → modo **adicionar**.
- `AddProductScreen(productId = 3)` → modo **editar**: busca o item com `buscarPorId`, preenche os campos e, ao salvar, chama `atualizar` mantendo o mesmo `id`.

Rotas novas: `edit_product/{productId}` e `edit_category/{categoryId}`. O botão de editar (lápis) aparece no card da lista e na tela de detalhes.

---

# Documentação do processo e das decisões

> ⚠️ **Preencham os trechos marcados com 📸/🎥** com prints e vídeos do app de vocês rodando.
> O enunciado deixa claro que texto sem o app funcionando não vale.

## 1. Como estava o projeto no Trabalho 2 e o que mudou?

No Trabalho 2, o `ShopHubRepository` era um `object` com duas `mutableStateListOf` (categorias e produtos),
com funções como `addProduct`, `removeProduct`, `toggleFavorite`. Tudo vivia só na memória: ao fechar o app, o que
foi adicionado ou removido se perdia, e não existia edição.

No Trabalho 3:
- O `ShopHubRepository` foi **removido** e substituído por Room (`AppDatabase` + 2 DAOs).
- As data classes `Category` e `Product` viraram `@Entity` (acrescentamos `@PrimaryKey(autoGenerate = true)` e `id = 0` como padrão).
- Todas as telas passaram a buscar/gravar no banco.
- Foi criada a **edição** (antes não existia) e as telas de detalhes passaram a buscar pelo `id`.

📸 _Print do app do Trabalho 2 (listas) e do app atual:_ `docs/...`

## 2. Entities, DAOs e Database — e por quê?

**`Category`** (`categorias`): `id`, `name`, `description`, `featured`.
**`Product`** (`produtos`): `id`, `name`, `price`, `categoryId`, `description`, `isFavorite`.

Escolhemos exatamente os campos que já existiam no Trabalho 2, para não mudar a interface. O `categoryId` do produto
guarda o `id` da categoria (relação simples, sem `@ForeignKey`, pois o enunciado não exige e o comportamento do T2 era o mesmo).
O `price` continuou como `String`, como já era no Trabalho 2.

**DAOs:** `CategoryDao` e `ProductDao`, cada um com `inserir`, `buscarTodos`, `atualizar`, `deletar` (todos `suspend`) mais `buscarPorId`.

**Database:** `AppDatabase` (`version = 1`), Singleton. Usamos um `RoomDatabase.Callback.onCreate` para inserir os 3+3 itens iniciais
só na **primeira** criação do arquivo do banco (assim o app não nasce vazio e o que o usuário apagar continua apagado).

## 3. Queries do DAO além dos 4 básicos

- `buscarPorId(id)` (em ambos): usada nas telas de **detalhes** e no **modo edição** dos formulários. Retorna nulo se o item não existir (ex.: foi removido), e a tela mostra "não encontrado" em vez de quebrar.
- `ProductDao.buscarPorCategoria(categoryId)`: usada na tela de detalhes da categoria, para listar os produtos relacionados direto do banco.

## 4. Dificuldades ao migrar de `mutableStateListOf` para Room

Pontos técnicos que apareceram (ajustem/complementem com o que realmente aconteceu com vocês):

- **A lista não atualizava sozinha.** Com `mutableStateListOf`, mexer na lista já redesenhava a tela. Com Room, o dado vem de uma consulta, então guardamos o resultado em um `mutableStateOf(List)` e **recarregamos** (`carregar()`) depois de cada inserir/atualizar/deletar.
- **Thread.** Toda chamada ao DAO fica dentro de `CoroutineScope(Dispatchers.IO).launch`. Mas a **navegação** (`onSave()`) não pode ser feita na IO, então usamos `withContext(Dispatchers.Main) { onSave() }` depois de salvar.
- **Telas de detalhes recebiam o objeto/ID da memória.** Agora recebem só o `id` e buscam com `buscarPorId`; foi preciso tratar o estado "carregando" para a tela não piscar "não encontrado" antes da consulta terminar.
- **Edição.** Para atualizar, é preciso manter o mesmo `id` (senão o `@Update` não acha a linha). Por isso o formulário carrega o item original e usa `copy(...)`.
- **Arquivo `Product.kt` sem `package`.** No código base do Trabalho 2, `Product.kt` estava sem a linha `package com.example.shophub.model`, o que quebrava os imports das telas. Corrigimos ao transformá-lo em `@Entity`.

**Dificuldades individuais do trio:** _(cada integrante: 2–3 linhas sobre onde travou e como resolveu)_
- Integrante 1: …
- Integrante 2: …
- Integrante 3: …

## 5. Teste de persistência (aceitação)

Passo a passo do teste:
1. Abrir **Produtos** → **Adicionar novo produto** → preencher e salvar.
2. Confirmar que aparece na lista.
3. **Fechar o app completamente** (tirar da lista de apps recentes).
4. Abrir de novo → **Produtos** → o item continua lá.

🎥 _Vídeo do teste de persistência:_ _(link / arquivo em `docs/`)_
📸 _Prints: antes de fechar / depois de reabrir_

## 6. Demonstração do CRUD

🎥 _Vídeo curto mostrando: adicionar, editar, remover, abrir detalhes (que busca do banco) e fechar/reabrir._
