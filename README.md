# Narutoto: Clans Addon

Addon para **Narutoto: Ninja World 0.4.1** (Forge 1.20.1) que adiciona dois clãs novos: **Namikaze** e **Ōtsutsuki**.

> Status: o código foi validado quanto à sintaxe, mas **ainda não foi compilado nem testado dentro do jogo**
> (o ambiente onde foi escrito não tinha JDK completo nem acesso à internet). Veja "Primeiro teste" abaixo.

## Como compilar

Requisitos: JDK 17.

1. Copie `gradlew`, `gradlew.bat` e `gradle/wrapper/gradle-wrapper.jar` do MDK oficial do Forge 1.20.1
   (ou use um Gradle 8.1.1+ instalado).
2. Rode `./gradlew build` (ou `gradle build`).
3. O jar sai em `build/libs/narutoto_clans-1.0.0.jar`.
4. Coloque o jar na pasta `mods/` junto com o `narutoto-0_4_1-forge.jar` e as dependências do próprio Narutoto
   (GeckoLib, PlayerAnimator, Pehkui, Photon).

O addon **não precisa do jar do Narutoto para compilar**: toda a integração é feita por reflexão em
`bridge/NarutotoBridge.java` (único arquivo a ajustar se o mod base mudar internamente).

## Como entrar nos clãs

A tela de seleção de ninja do Narutoto é código fechado do mod base, então o addon usa **pergaminhos**
(aba criativa "Narutoto: Clãs") e comandos:

| Item / comando | O que faz |
|---|---|
| Pergaminho do Clã Namikaze | Clique direito: entra no clã (só quem ainda não tem clã) |
| Pergaminho do Clã Ōtsutsuki | Clique direito: entra no clã (só quem ainda não tem clã) |
| Fruto de Chakra | Comer: chakra máximo permanente + pontos de atributo/jutsu |
| `/narutoclans info` | Mostra seu clã |
| `/narutoclans kunai` | (Namikaze) recupera a Kunai Hiraishin |
| `/narutoclans set <jogador> <namikaze\|otsutsuki>` | (op) força o clã |
| `/narutoclans remove <jogador>` | (op) remove do clã do addon |
| `/narutoclans fruit <jogador>` | (op) aplica os efeitos de um Fruto |

Receitas de sobrevivência existem para os três itens (ver `data/narutoto_clans/recipes`).
O clã é uma *advancement* filha de `narutoto:begin`, igual aos clãs do mod base: o botão de reset do
Narutoto remove o clã do addon automaticamente.

## Clã Namikaze

* Ao entrar: aprende o **Hiraishin no Jutsu** (mesmo fluxo do mod base: advancement `narutoto:learn_hiraishin`),
  o jutsu já é colocado no primeiro slot livre do livro de jutsus e recebe uma **Kunai Hiraishin** (`narutoto:hiraishin_kunai_weapon`).
* Renasce com uma Kunai Hiraishin se não tiver nenhuma (configurável).
* Passiva: +15% de velocidade (configurável).

## Clã Ōtsutsuki

| Recurso do pedido | Implementação |
|---|---|
| Chakra quase ilimitado | Chakra máximo 1.000.000 + regeneração de 2%/s (configurável) |
| Byakugan | Advancement `narutoto:learn_byakugan` (usa o Byakugan real do mod base) |
| Rinnegan | Advancement `narutoto:learn_rinnegan` + pergaminhos Shinra Tensei, Banshō Ten'in, Chibaku Tensei |
| Tenseigan | Modo ativável (tecla **N**): Força III, Resistência II, Velocidade II, Regeneração; gasta chakra |
| Jōgan | Modo ativável (tecla **N**, segundo toque): visão noturna + revela seres vivos através de paredes |
| Viagem dimensional | Tecla **B**: Yomotsu Hirasaka, teleporta até onde você olha (96 blocos), gasta chakra |
| Voo | Voo livre em survival |
| Adaptação espacial | Sem afogamento, sem dano de queda/congelamento/asfixia em bloco, resistência a fogo |
| Absorção de técnicas | Dano de jutsus do Narutoto vira chakra e o atacante recebe o dobro de volta |
| Marca da Karma | Ao morrer, renasce com vida cheia (recarga de 10 min; `/kill` e o void não são evitados) |
| Fruto de Chakra | Item consumível (receita + `/narutoclans fruit`) |
| Dominação elemental | Concede as 5 naturezas de chakra |

Não implementados: **Rinne Sharingan** (o mod base não tem os assets/mecânica correspondentes) e a transformação
de outro jogador em Fruto de Chakra (existe o item e a receita, mas não o "sacrifício").

As teclas B e N podem ser alteradas em Controles (categoria "Narutoto: Clãs") caso conflitem com outras do Narutoto.

## Configuração

`config/narutoto_clans-common.toml` — todos os números acima (chakra, recargas, custos, alcance, bônus) são configuráveis.

## Primeiro teste (recomendado)

1. Mundo novo com Narutoto + addon, `/give @s narutoto_clans:namikaze_clan_scroll`, clique direito.
   Confirme: advancement "Learn Hiraishin", jutsu no livro, kunai no inventário.
2. Outro personagem (`/narutoclans set <você> otsutsuki`): confirme chakra, voo, teclas B/N, Karma.
3. Se algo da integração falhar, o log mostra `Integracao com Narutoto falhou em '...'` com o campo exato.
