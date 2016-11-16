:-	dynamic proximo_passo/3.
:-	dynamic tamanho_mapa/2.
:-	dynamic mapa_real/2.
:-	dynamic inimigo/2.
:-	dynamic total_ouros/1.
:-	dynamic terminou/1.
:-	dynamic sensor/3.
:-	dynamic posicao/2.
:-	dynamic orientacao/1.
:-	dynamic score/1.
:-	dynamic municao/1.
:-	dynamic energia/1.
:- 	dynamic	fronteira/2.
:-  dynamic certeza/2.
:-	dynamic observado/2.
:-	dynamic esta_atirando/1.
:-	dynamic dano_dado/1.

:- 	dynamic consumido/2.

:- dynamic debuga/4.

terminou(nao).

acao(decidir) :-	total_ouros(X), X =< 0, limpa_proximo_passo(), certeza( (Z, W) , saida ), adiciona_proximo_passo((Z, W), 0 , sair ) , !.
acao(decidir) :-	posicao(X, Y), certeza( ( X , Y ) , ouro ) , acao(pegar_objeto), acao(decidir), !.
acao(decidir) :-	energia(V), V =< 50, ( posicao(X, Y), certeza( ( X , Y ) , power_up ) , acao(pegar_objeto) , acao(decidir) ; ( certeza( ( _ , _ ) , power_up ) , limpa_proximo_passo() , findall(_, encontrar_mais_proximo(power_up) ,_ ) , !) ).
acao(decidir) :-	agente_olhando_para( X , Y ) , fronteira( X , Y ) , limpa_proximo_passo() , adiciona_proximo_passo((X, Y), 1 , mover ) , !.
acao(decidir) :-	( fronteira( _ , _ ) , limpa_proximo_passo(), encontrar_mais_proximo() ), !.
acao(decidir) :-	sensor( ( _ , _ ) , ( _ , _ ) , inimigo(_,_)), limpa_proximo_passo(), encontrar_mais_proximo( inimigo(_,_) , mover ) , !.
acao(decidir) :-	certeza( ( _ , _ ) , inimigo(_,_) ), esta_atirando(sim), limpa_proximo_passo(), encontrar_mais_proximo( inimigo(_,_), atirar ) , !.
acao(decidir) :-	certeza( ( _ , _ ) , inimigo(_,_) ), municao(X), X >= 3, not(esta_atirando(sim)), limpa_proximo_passo(), encontrar_mais_proximo( inimigo(_,_), atirar ) , !.
acao(decidir) :-	certeza( ( _ , _ ) , inimigo(_,_) ), limpa_proximo_passo(), encontrar_mais_proximo( inimigo(_,_) ) , !.
acao(decidir) :-	score(X), X > 1500, certeza( (Z, W) , saida ), limpa_proximo_passo(), adiciona_proximo_passo((Z, W), 0 , sair ) , ! .
acao(decidir) :-	sensor( ( _ , _ ) , ( _ , _ ) , teleporte), limpa_proximo_passo(), encontrar_mais_proximo( teleporte , mover ) , !.

acao(mover_para_frente) :- 	mover(Z,W) , adiciona_a_score(-1) , ( consequencias() ; true ) , observar(Z,W) , !.

acao(virar_a_direita) :- orientacao(cima), retract(orientacao(cima)), assert(orientacao(direita)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(esquerda), retract(orientacao(esquerda)), assert(orientacao(cima)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(baixo), retract(orientacao(baixo)), assert(orientacao(esquerda)), adiciona_a_score(-1), !.
acao(virar_a_direita) :- orientacao(direita), retract(orientacao(direita)), assert(orientacao(baixo)), adiciona_a_score(-1), !.

acao(pegar_objeto) :- posicao( X , Y ) , possui_objeto( X , Y , ouro ) , adiciona_a_score( 1000 ) , assert( consumido( X , Y ) ), remove_mapa_real( X , Y ) , ( definir_certeza( X , Y ) ; true ) ,  reduzir_total_ouros() , !.
acao(pegar_objeto) :- posicao( X , Y ), possui_objeto( X , Y , power_up ) ,  recuperar_vida() , assert( consumido( X , Y ) ) , remove_mapa_real( X , Y ) , ( definir_certeza( X , Y ) ; true ) , !. 

acao(subir) :- posicao( X , Y ), mapa_real( ( X, Y ) , saida ) , termina(saiu_do_labirinto).

acao(atirar) :-	municao( X ) , X > 0 , adiciona_a_score( -10 ) , remove_municao(), simula_tiro() .

consequencias() :- posicao( X , Y ) , possui_objeto( X , Y , Objeto ) , Objeto = buraco , adiciona_a_score( -1000 ) , assert( consumido( X , Y ) ) , termina( morreu_para_buraco ).
consequencias()	:- posicao( X , Y ) , possui_objeto( X , Y , Objeto ) , Objeto = inimigo( D , _ ) , tomar_dano( D ).
consequencias()	:- posicao( X , Y ) , possui_objeto( X , Y , Objeto ) , Objeto = teleporte , observar( X , Y ) , teleporta() , consequencias().
consequencias()	:- posicao( X , Y ) , observar( X , Y ).

simula_tiro() :- agente_olhando_para( X , Y ) , mapa_real( ( X , Y ) , inimigo( _ , _ ) ) , dar_dano_inimigo( X , Y ).

dar_dano_inimigo(X, Y) :-	mapa_real( ( X , Y ), inimigo( D , V ) ) , Dano is ( random( 30 ) + 20 ), assert(dano_dado(Dano)) , W is V - Dano, remove_mapa_real( X, Y ) , ( ( W > 0, adicionar_esta_atirando(), extern_adicionar_ao_mapa( ( X , Y ) , inimigo( D , W ) ) ) ; assert( fronteira( X, Y ) ), remove_esta_atirando(), assert( consumido( X , Y ) ),  true ) , ! .

adicionar_esta_atirando() :-	( esta_atirando(sim) ; assert(esta_atirando(sim)) ) , !.
remove_esta_atirando() :-	( ( esta_atirando(sim) , retract(esta_atirando(sim)) ) ; true ) , !.

agente_olhando_para(X, Y) :-	orientacao(cima), posicao(Z, W), X is Z, Y is W + 1, (eh_no_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(baixo), posicao(Z, W), X is Z, Y is W - 1, (eh_no_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(direita), posicao(Z, W), X is Z + 1, Y is W, (eh_no_mapa(X, Y) ; write('Parede')), !.
agente_olhando_para(X, Y) :-	orientacao(esquerda), posicao(Z, W), X is Z - 1, Y is W, (eh_no_mapa(X, Y) ; write('Parede')), !.	
							
% Observa e sente uma região
											
observar( X , Y )	:- ( ( not( observado( X , Y ) ) , assert( observado( X , Y ) ) ) ; true )
					, ( definir_certeza( X , Y ) ; true )
					, ( criar_sensores_em( X , Y ) ; true )				
					, ( validar_fronteiras() ; true ).
					

					
% Define a certeza do conteudo do local X , Y					

definir_certeza( X , Y )	:- findall( _ , retract( certeza( ( X, Y ) , _ ) ) , _ )
							, ( ( mapa_real( ( X , Y ) , Objeto ) , assert( certeza( ( X, Y ) , Objeto ) ) ) ; ( not( certeza( ( X, Y ) , _ ) ) , assert( certeza( ( X, Y ) , nada ) ) ) )
							, retract( fronteira( X , Y ) )
							, findall( _ , intern_remover_completamente_sensor_com_alvo( X , Y , Objeto ) , _ ) 
							, findall( _ , intern_remover_parcialmente_sensor( X , Y , Objeto ) , _ ).
							
							
intern_remover_completamente_sensor_com_alvo( TX , TY , Objeto )	:- sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) , remover_sensor_completamente( PX , PY , Objeto ).

intern_remover_parcialmente_sensor( TX , TY , Objeto )				:- ObjetoAlvo \= Objeto , remover_parcialmente_sensor( TX , TY , ObjetoAlvo ).



% ADICAO DE SENSORES

criar_sensores_em( X , Y )  						:- findall( _ , intern_criar_sensores_perigos_em( X , Y ) , _ ) , validar_sensores().

intern_criar_sensores_perigos_em( X , Y )  			:- eh_adjacente( X , Y , X2 , Y2 ) ,  not( eh_conhecido( X2 , Y2 ) )  ,  eh_ameaca( X2 , Y2 )  ,  possui_objeto( X2 , Y2 , Objeto )  ,  findall(_ , intern_criar_sensores_cond( X , Y , Objeto ) , _ ).

intern_criar_sensores_cond( X , Y , Objeto )		:- eh_adjacente( X , Y , X2 , Y2 ) ,  not( eh_conhecido( X2 , Y2 ) )  ,  not( eh_fronteira( X2 , Y2 ) )  ,  intern_criar_sensor( X , Y , X2 , Y2 , Objeto ).

intern_criar_sensor( PX , PY , TX , TY , Objeto )	:-	not( sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) ) , assert( sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) ).

							

							
% REMOCAO DE SENSORES

remover_sensor_completamente( PX , PY , Objeto )					:- findall( _ , retract( sensor( ( PX , PY ) , ( _ , _ ) , Objeto ) ) , _ ).

remover_parcialmente_sensor( TX , TY , Objeto )						:- findall(_,intern_remover_parcialmente( TX , TY , Objeto ),_) , validar_sensores().

remover_parcialmente_sensor( PX , PY , TX , TY , Objeto )			:- sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) , retract( sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) ) , validar_sensores().
remover_parcialmente_sensor2( PX , PY , TX , TY , Objeto )			:- sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) , retract( sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) ) , validar_sensores().

intern_remover_parcialmente( TX , TY , Objeto )						:- sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) , retract( sensor( ( PX , PY ) , ( TX , TY ) , Objeto ) ).




% VALIDA SENSORES: CHECA SE PODEM SER REMOVIDOS E SE DESCOBRIMOS ALGO

validar_sensores()						:- findall( _ , intern_validar_sensores_coerencia() , _ )
										, findall( _ , intern_validar_sensores_vazio() , _ )
										, findall( _ , intern_validar_sensores_certos() , _ )
										, findall( _ , intern_validar_sensores_errados() , _ )
										, findall( _ , intern_validar_sensores_certeza() , _ ).

										
intern_validar_sensores_coerencia()		:-	sensor( ( S1PX , S1PY )  , ( TX , TY ) , S1Obj )
										, S2Obj \= S1Obj
										, ( S2PX \= S1PX , S2PY \= S1PY )
										, sensor( ( S2PX , S2PY )  , ( TX , TY ) , S2Obj )
										, findall(_,remover_parcialmente_sensor( TX , TY , S1Obj ),_)
										, findall(_,remover_parcialmente_sensor( TX , TY , S2Obj ),_).
										
% 		, not( ( S1PX is 3 , S1PY is 1 ) )				
							
intern_validar_sensores_vazio()			:-	sensor( ( S1PX , S1PY )  , ( TX , TY ) , Objeto )
										, eh_adjacente( TX , TY , AX , AY )
										, observado( AX , AY )
										, ( AX \= S1PX , AY \= S1PY )
										, not( sensor( ( AX , AY )  , ( TX , TY ) , Objeto ) )
										, remover_parcialmente_sensor( S1PX , S1PY , TX , TY , Objeto ).
										

intern_validar_sensores_certos()		:-	sensor( ( S1PX , S1PY )  , ( TX , TY ) , Objeto )
										, certeza( ( TX  ,TY ) , Objeto )
										, remover_sensor_completamente( S1PX , S1PY , Objeto ).	

										
intern_validar_sensores_errados()		:-	sensor( ( S1PX , S1PY )  , ( TX , TY ) , Objeto )
										, certeza( ( TX  ,TY ) , ObjetoCorreto )
										, ObjetoCorreto \= Objeto
										, remover_parcialmente_sensor( S1PX , S1PY , TX , TY , Objeto ).												
										
										
intern_validar_sensores_certeza()		:-	sensor( ( S1X , S1Y ), ( S1TX , S1TY ), Objeto)	
										, not( ( sensor( ( S1X , S1Y ) , ( S2TX , S2TY ) , Objeto )
										, ( S2TX \= S1TX ; S2TY \= S1TY ) ) )
										, definir_certeza( S1TX , S1TY )
										, validar_sensores().

										
										
% VALIDA REGIÃO QUE É FRONTEIRA	
								
validar_fronteiras()					:- findall( _ , intern_validar_fronteiras() , _ ).		

intern_validar_fronteiras()				:- observado( X , Y ), eh_adjacente( X , Y , X2 , Y2 )
										, not( eh_perigosa( X2 , Y2 ) )
										, not( eh_fronteira( X2 , Y2 ) )
										, not( eh_conhecido( X2 , Y2 ) )
										, assert( fronteira( X2 , Y2 ) ).
										
										
% CHECKS RECORRENTES									

eh_conhecido( X , Y ) 				:- certeza( ( X , Y ) , _ ).

eh_fronteira( X , Y ) 				:- fronteira( X , Y ).

eh_perigosa( X , Y ) 				:- sensor( (_,_) , (X,Y) , _ ).

eh_no_mapa(X, Y) 			 		:- tamanho_mapa(A, B), X < A, X >= 0, Y < B, Y >= 0.	

eh_adjacente( X , Y , X2 , Y2 )		:- X2 is X - 1 , Y2 is Y , eh_no_mapa( X2 , Y2 ).
eh_adjacente( X , Y , X2 , Y2 )		:- X2 is X , Y2 is Y + 1 , eh_no_mapa( X2 , Y2 ).
eh_adjacente( X , Y , X2 , Y2 )		:- X2 is X + 1 , Y2 is Y , eh_no_mapa( X2 , Y2 ).
eh_adjacente( X , Y , X2 , Y2 )		:- X2 is X , Y2 is Y - 1 , eh_no_mapa( X2 , Y2 ).

eh_ameaca( Objeto )					:-  Objeto \= power_up , Objeto \= ouro , Objeto \= nada. 
eh_ameaca( X , Y )					:-	mapa_real( ( X , Y ) , Objeto ) , eh_ameaca( Objeto ).

possui_objeto( X , Y , Objeto )		:-	mapa_real( ( X , Y ) , Objeto ).



% FUNÇÕES NÃO CLASSIFICADAS

distancia_manhatam((X1,Y1), (X2, Y2), C) :-	mod(X1 - X2, X), mod(Y1 - Y2, Y), C is (X + Y).

mod(X, Y) :- (X < 0, Y is (-X) , !) ; (Y is X , !).

atualizar_posicao(X, Y) :-	posicao(Z, W), retract(posicao(Z, W)), assert(posicao(X, Y)).

atualizar_energia(X)	:-	retract(energia(_)), assert(energia(X)).

adiciona_proximo_passo((X, Y), Custo , Tipo ) :- assert(proximo_passo( ( X , Y) , Custo , Tipo ) ).

limpa_proximo_passo() :- findall(_,retract(proximo_passo(_,_,_)),_).

encontrar_mais_proximo() :-	findall(_,menor_distancia(),_), !.
encontrar_mais_proximo( Objeto ) :-	findall(_,menor_distancia( Objeto ),_), !.
encontrar_mais_proximo( Objeto, Tipo ) :-	findall(_,menor_distancia( Objeto, Tipo ),_), !.

menor_distancia() 			:-	fronteira( X , Y ) , posicao(Z, W), distancia_manhatam( ( Z , W ) , ( X , Y ) , Custo ) , adiciona_proximo_passo( ( X , Y ) , Custo , mover ).
menor_distancia( Objeto ) 	:-	certeza( ( X , Y ) , Objeto ) , posicao( Z , W ), distancia_manhatam( ( Z , W ) , ( X , Y ) , Custo ) , adiciona_proximo_passo( ( X , Y ) , Custo , mover ).
menor_distancia( Objeto, mover ) 	:-	sensor( ( _ , _ ) , ( X , Y ) , Objeto ) , posicao( Z , W ), distancia_manhatam( ( Z , W ) , ( X , Y ) , Custo ) , adiciona_proximo_passo( ( X , Y ) , Custo , mover ).
menor_distancia( Objeto, atirar ) 	:-	certeza( ( X , Y ) , Objeto ) , posicao( Z , W ), distancia_manhatam( ( Z , W ) , ( X , Y ) , Custo ) , adiciona_proximo_passo( ( X , Y ) , Custo , atirar ).

mover(Z,W)		:- orientacao(direita), posicao(X, Y), Z is X + 1, W is Y, eh_no_mapa(Z, W), atualizar_posicao(Z, W), !.
mover(Z,W)		:- orientacao(esquerda), posicao(X, Y), Z is X - 1, W is Y, eh_no_mapa(Z, W), atualizar_posicao(Z, W), !.
mover(Z,W)		:- orientacao(cima), posicao(X, Y), Z is X, W is Y + 1, eh_no_mapa(Z, W), atualizar_posicao(Z, W), !.
mover(Z,W)		:- orientacao(baixo), posicao(X, Y), Z is X, W is Y - 1, eh_no_mapa(Z, W), atualizar_posicao(Z, W), !.

reduzir_total_ouros()	:-	total_ouros( O ) , retract( total_ouros(_) ) , K is O - 1 , assert( total_ouros( K ) ).

teleporta()				:- tamanho_mapa( Tx , Ty ) , Z is random( Tx ) , W is random( Ty ) , atualizar_posicao( Z , W ).

termina(Objeto) 		:- retract( terminou(_) ) , assert( terminou(Objeto) ).

remove_mapa_real(X, Y)	:-	retract( mapa_real( ( X , Y ) , _ ) ).

adiciona_a_score(X)		:- score(Y), W is Y + X, findall(_,retract(score(_)),_), assert(score(W)).

remove_municao()		:-	municao(Y), W is Y - 1, retract(municao(_)), assert(municao(W)), !.

recuperar_vida()		:- energia(Y), W is Y + 20, (W =< 100 ; W is 100), atualizar_energia( W ).

tomar_dano(X)			:-	energia(Y), W is Y - X, atualizar_energia( W ), adiciona_a_score( -X ), ( testar_morte() ; true ).

testar_morte()			:-	energia(W), W =< 0, adiciona_a_score( -1000 ), termina( morreu_para_inimigo ), !.



% PARA COMUNICAÇÃO COM O JAVA

extern_adicionar_ao_mapa((X, Y), Objeto) :- assert( mapa_real( ( X , Y ) , Objeto ) ).

extern_instanciar_tamanho_mapa(X, Y) :-	assert(tamanho_mapa(X, Y)).

							