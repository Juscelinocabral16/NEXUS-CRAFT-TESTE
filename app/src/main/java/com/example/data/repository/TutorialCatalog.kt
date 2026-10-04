package com.example.data.repository

import com.example.R
import com.example.data.model.BuildDifficulty
import com.example.data.model.MaterialItem
import com.example.data.model.StepIllustrationType
import com.example.data.model.Tutorial
import com.example.data.model.TutorialCategory
import com.example.data.model.TutorialStep

object TutorialCatalog {
    val tutorials: List<Tutorial> = listOf(
        Tutorial(
            id = "casa-moderna-simples",
            title = "Casa Moderna Simples",
            shortDescription = "Arquitetura contemporânea minimalista em concreto preto e branco com janelão vertical e arbustos.",
            fullDescription = "Esta casa moderna compacta é a escolha perfeita para quem busca estilo contemporâneo com baixo consumo de materiais. A arquitetura combina um bloco vertical em concreto branco com janela do chão ao teto e um módulo superior em concreto preto carvão com janela panorâmica de canto, entrada recuada aconchegante e cerca viva de folhas de carvalho.",
            category = TutorialCategory.HOUSES,
            difficulty = BuildDifficulty.BEGINNER,
            estimatedTime = "20 min",
            dimensions = "7x7 blocos (Altura: 9)",
            heroImageRes = R.drawable.img_casa_moderna_simples,
            isFeatured = true,
            tags = listOf(
                "casa", "moderna", "simples", "concreto", "compacta",
                "minimalista", "iniciante", "preto e branco", "preto", "branco", "vidro"
            ),
            materials = listOf(
                MaterialItem("mat-cms-1", "Concreto Branco (White Concrete)", 96, "concrete"),
                MaterialItem("mat-cms-2", "Concreto Preto ou Cinza Escuro", 72, "concrete"),
                MaterialItem("mat-cms-3", "Painéis de Vidro Azul Claro ou Transparente", 28, "glass"),
                MaterialItem("mat-cms-4", "Lajes de Pedra Lisa (Smooth Stone)", 16, "stone"),
                MaterialItem("mat-cms-5", "Folhas de Carvalho (Oak Leaves)", 18, "leaf"),
                MaterialItem("mat-cms-6", "Porta de Carvalho Escuro", 1, "door"),
                MaterialItem("mat-cms-7", "Lanternas ou Lâmpadas de Redstone", 4, "light"),
                MaterialItem("mat-cms-8", "Cama e Baú para Interior", 2, "decor")
            ),
            steps = listOf(
                TutorialStep(
                    stepNumber = 1,
                    title = "Base Compacta 7x7 e Entrada Recuada",
                    description = "Nivele uma área 7x7 no solo. Posicione as lajes de pedra lisa formando o piso do caminho e reserve a entrada frontal recuada em 2 blocos para criar sombra e profundidade sob o segundo andar.",
                    builderTip = "Lajes de pedra lisa dão um acabamento de calçada muito mais limpo que blocos inteiros.",
                    requiredBlocks = listOf("Lajes de Pedra Lisa", "Pá"),
                    layerInfo = "Camada Y: Nível do Solo (Y: 0)",
                    illustrationType = StepIllustrationType.FOUNDATION_GRID,
                    imageRes = R.drawable.img_casa_moderna_simples
                ),
                TutorialStep(
                    stepNumber = 2,
                    title = "Estrutura da Torre Branca Vertical",
                    description = "No lado direito da casa, erga a torre vertical retangular de Concreto Branco com 9 blocos de altura total e 3 blocos de largura. Deixe um vão central de 1 bloco de largura por 7 de altura na face frontal.",
                    builderTip = "O concreto branco puro cria um contraste perfeito com o concreto preto do bloco vizinho.",
                    requiredBlocks = listOf("Concreto Branco"),
                    layerInfo = "Camadas Y: 1 a 9",
                    illustrationType = StepIllustrationType.PILLARS_FRAME,
                    imageRes = R.drawable.img_casa_moderna_simples
                ),
                TutorialStep(
                    stepNumber = 3,
                    title = "Janelão Vertical Panorâmico",
                    description = "Preencha a fenda de 1x7 blocos aberta na torre branca com painéis de vidro azul claro ou transparente. O vidro deve descer do topo até quase o nível da calçada.",
                    builderTip = "Painéis de vidro criam um recuo elegante de meio bloco em relação à moldura de concreto.",
                    requiredBlocks = listOf("Painéis de Vidro Azul Claro"),
                    layerInfo = "Camadas Y: 1 a 8",
                    illustrationType = StepIllustrationType.WALLS_WINDOWS,
                    imageRes = R.drawable.img_casa_moderna_simples
                ),
                TutorialStep(
                    stepNumber = 4,
                    title = "Cubo Superior em Concreto Preto",
                    description = "No lado esquerdo superior (a partir da altura Y: 4), projete o cubo em balanço de Concreto Preto com 4x4 blocos. Ele fica suspenso sobre a entrada térrea, protegendo a porta da chuva.",
                    builderTip = "Estruturas em balanço (cantilever) são a marca registrada da arquitetura moderna minimalista.",
                    requiredBlocks = listOf("Concreto Preto"),
                    layerInfo = "Camadas Y: 4 a 8",
                    illustrationType = StepIllustrationType.WALLS_WINDOWS,
                    imageRes = R.drawable.img_casa_moderna_simples
                ),
                TutorialStep(
                    stepNumber = 5,
                    title = "Janela Panorâmica de Canto (Em 'L')",
                    description = "No piso superior do cubo preto, retire os blocos da quina para abrir uma ampla janela panorâmica em formato de 'L' de 3x2 blocos e instale os painéis de vidro.",
                    builderTip = "Janelas de canto ampliam visualmente o interior de cômodos compactos e deixam a luz do sol entrar.",
                    requiredBlocks = listOf("Painéis de Vidro", "Concreto Preto"),
                    layerInfo = "Camadas Y: 5 a 6",
                    illustrationType = StepIllustrationType.WALLS_WINDOWS,
                    imageRes = R.drawable.img_casa_moderna_simples
                ),
                TutorialStep(
                    stepNumber = 6,
                    title = "Porta Recuada, Cerca Viva e Iluminação",
                    description = "No térreo recuado sob o cubo preto, instale a porta de madeira de carvalho escuro virada para fora. Ao redor do caminho e na lateral esquerda, plante a cerca viva de folhas de carvalho podadas e adicione iluminação quente.",
                    builderTip = "Coloque uma lanterna embutida sob as folhas de carvalho para iluminar o jardim sem fontes de luz visíveis!",
                    requiredBlocks = listOf("Porta de Carvalho Escuro", "Folhas de Carvalho", "Lanternas"),
                    layerInfo = "Camada Y: 1 (Acabamentos)",
                    illustrationType = StepIllustrationType.INTERIOR_LIGHTS,
                    imageRes = R.drawable.img_casa_moderna_simples
                )
            )
        )
    )
}
