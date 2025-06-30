# ========================================
# CÓDIGO MIPS UNIVERSAL - SIN HARDCODEO
# Funciona con cualquier nombre de función
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    prompt_string: .asciiz "Ingrese texto: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Constantes booleanas del lenguaje
    true_const:   .word 1
    false_const:  .word 0
    luna_const:   .word 1    # luna = true
    sol_const:    .word 0    # sol = false

    # Variables del programa
    t4_var: .word 0
    a_var: .word 0
    b_var: .word 0
    arbol_var: .word 0
    resultado_var: .word 0
    planta_var: .word 0
    t1_var: .word 0
    t2_var: .word 0
    t3_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/ab_intermediate.txt
suma:
    # 🚀 UNIVERSAL: Prólogo para suma
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # 🚀 UNIVERSAL: Guardar parámetros de suma
    sw $a0, -4($fp)   # a local
    sw $a0, a_var     # a global
    sw $a1, -8($fp)   # b local
    sw $a1, b_var     # b global

    # Inicio de función
    # DECLARE a INT
    # DECLARE b INT
    # t1 = a + b
    lw $t1, -4($fp)   # a local
    lw $t2, -8($fp)   # b local
    add $t0, $t1, $t2
    sw $t0, t1_var

    # DECLARE resultado INT
    # resultado = t1
    lw $t0, t1_var
    sw $t0, resultado_var

    # RETURN resultado
    lw $v0, resultado_var
    # Valor de retorno en $v0
    j exit_suma


# 🚀 UNIVERSAL: Epílogo para suma
exit_suma:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

main:
    # 🚀 UNIVERSAL: Prólogo para main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp
    # Reservar espacio para variables locales
    addi $sp, $sp, -16

    # Función sin parámetros o no detectados

    # Inicio de función
    # DECLARE a INT
    # a = 10
    li $t0, 10
    sw $t0, a_var

    # DECLARE b INT
    # b = 5
    li $t0, 5
    sw $t0, b_var

    # t2 = a + b
    lw $t1, a_var
    lw $t2, b_var
    add $t0, $t1, $t2
    sw $t0, t2_var

    # DECLARE arbol INT
    # arbol = t2
    lw $t0, t2_var
    sw $t0, arbol_var

    # t3 = a - b
    lw $t1, a_var
    lw $t2, b_var
    sub $t0, $t1, $t2
    sw $t0, t3_var

    # DECLARE planta INT
    # planta = t3
    lw $t0, t3_var
    sw $t0, planta_var

    # WRITE arbol
    lw $a0, arbol_var
    jal print_int
    la $a0, nl
    jal print_string

    # WRITE planta
    lw $a0, planta_var
    jal print_int
    la $a0, nl
    jal print_string

    # PARAM 32
    li $a0, 32
    # ✅ UNIVERSAL: Parámetro 32 cargado en $a0

    # PARAM 44
    li $a1, 44
    # ✅ UNIVERSAL: Parámetro 44 cargado en $a1

    # t4 = CALL suma 2
    jal suma
    sw $v0, t4_var

    # WRITE t4
    lw $a0, t4_var
    jal print_int
    la $a0, nl
    jal print_string


# 🚀 UNIVERSAL: Epílogo para main
exit_main:
    # Limpiar variables locales
    addi $sp, $sp, 16    # Liberar espacio de variables locales
    # Restaurar frame pointer y return address
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    li $v0, 10
    syscall


# Salida del programa
exit_program:
    li $v0, 10
    syscall
# ========================================
# FUNCIONES DEL SISTEMA
# ========================================

print_int:
    li $v0, 1
    syscall
    jr $ra

print_string:
    li $v0, 4
    syscall
    jr $ra

read_int:
    li $v0, 5
    syscall
    jr $ra

read_float:
    li $v0, 6
    syscall
    jr $ra

print_float_decimal:
    # $a0 contiene el flotante multiplicado por 100
    addi $sp, $sp, -4
    sw $a0, 0($sp)
    bgez $a0, positive_float
    li $v0, 11
    li $a0, 45    # ASCII de '-'
    syscall
    lw $a0, 0($sp)
    sub $a0, $zero, $a0
positive_float:
    li $t1, 100
    div $a0, $t1
    mflo $t2
    mfhi $t3
    move $a0, $t2
    li $v0, 1
    syscall
    li $v0, 11
    li $a0, 46    # ASCII de '.'
    syscall
    bge $t3, 10, print_decimal
    li $v0, 11
    li $a0, 48    # ASCII de '0'
    syscall
print_decimal:
    move $a0, $t3
    li $v0, 1
    syscall
    addi $sp, $sp, 4
    jr $ra

