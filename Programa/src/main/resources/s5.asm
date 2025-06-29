# ========================================
# CÓDIGO MIPS GENERADO AUTOMÁTICAMENTE
# Compilador - Proyecto 3
# Autores: Bayron Rodríguez & Gadir Calderón
# ========================================

.data
    # Strings del sistema
    nl:           .asciiz "\n"
    prompt_int:   .asciiz "Ingrese un entero: "
    prompt_float: .asciiz "Ingrese un float: "
    result_msg:   .asciiz "Resultado: "
    true_str:     .asciiz "true"
    false_str:    .asciiz "false"

    # Variables del programa
    a_var: .word 0
    b_var: .word 0
    temp_var: .word 0
    resultado_var: .word 0
    contador_var: .word 0
    i_var: .word 0
    j_var: .word 0
    k_var: .word 0
    t1_var: .word 0
    t2_var: .word 0

.text
.globl main

    # // Código Intermedio Generado
    # // Archivo: src/main/resources/s5_intermediate.txt
multiplicar:
    # Prólogo simplificado multiplicar
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # ✅ FIX s5: Guardar parámetros para multiplicar(a, b)
    sw $a0, a_var
    sw $a1, b_var
    # Parámetros a y b guardados

    # Inicio de función
    # DECLARE a INT
    # DECLARE b INT
    # t1 = a * b
    lw $t1, a_var
    lw $t2, b_var
    mul $t0, $t1, $t2
    sw $t0, t1_var

    # DECLARE temp INT
    # temp = t1
    lw $t0, t1_var
    sw $t0, temp_var

    # RETURN temp
    lw $v0, temp_var
    # Valor de retorno en $v0
    j exit_multiplicar


# Epílogo simplificado multiplicar
exit_multiplicar:
    move $sp, $fp
    lw $fp, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

main:
    # Prólogo simplificado main
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $fp, 0($sp)
    move $fp, $sp

    # Inicio de función
    # PARAM 4
    li $a0, 4
    # ✅ s1: Parámetro 4 cargado en $a0

    # PARAM 3
    li $a1, 3
    # Parámetro 3 cargado en $a1

    # t2 = CALL multiplicar 2
    jal multiplicar
    sw $v0, t2_var

    # DECLARE resultado INT
    # resultado = t2
    lw $t0, t2_var
    sw $t0, resultado_var

    # WRITE resultado
    lw $a0, resultado_var
    jal print_int
    la $a0, nl
    jal print_string


# Epílogo simplificado main
exit_main:
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

